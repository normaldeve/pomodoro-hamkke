#!/bin/bash

set -e

# 환경 변수 로드
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

echo "=== 무중단 배포 시작 ==="
echo "IMAGE_TAG: ${IMAGE_TAG}"

# 현재 활성화된 컨테이너 확인
if docker ps --filter "name=blue" --filter "status=running" | grep -q blue; then
    CURRENT_ACTIVE="blue"
    NEW_ACTIVE="green"
else
    CURRENT_ACTIVE="green"
    NEW_ACTIVE="blue"
fi

echo "현재 활성 컨테이너: $CURRENT_ACTIVE"
echo "새로 배포할 컨테이너: $NEW_ACTIVE"

# 새 이미지 pull
echo "새 이미지 다운로드 중..."
docker-compose pull $NEW_ACTIVE

# 새 컨테이너 시작 (Flyway 마이그레이션 자동 실행됨)
echo "[$NEW_ACTIVE] 컨테이너 시작 중 (Flyway 마이그레이션 포함)..."
docker-compose up -d $NEW_ACTIVE

# Health check
echo "[$NEW_ACTIVE] Health check 대기 중..."
for i in {1..60}; do
    if docker exec $NEW_ACTIVE curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "[$NEW_ACTIVE] Health check 성공!"
        break
    fi

    if [ $i -eq 60 ]; then
        echo "[$NEW_ACTIVE] Health check 실패. 배포 중단."
        echo "=== 애플리케이션 로그 ==="
        docker-compose logs --tail=100 $NEW_ACTIVE
        echo "=== Flyway 마이그레이션 로그 확인 ==="
        docker exec mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "SELECT * FROM ${DB_NAME}.flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;"
        docker-compose stop $NEW_ACTIVE
        exit 1
    fi

    echo "[$NEW_ACTIVE] Health check 시도 $i/60..."
    sleep 2
done

# Flyway 마이그레이션 상태 확인
echo "=== Flyway 마이그레이션 상태 확인 ==="
docker exec mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "SELECT installed_rank, version, description, success FROM ${DB_NAME}.flyway_schema_history ORDER BY installed_rank;"

# Nginx 설정 변경
echo "Nginx 설정 변경 중..."
sed -i.bak "s/server $CURRENT_ACTIVE:8080/server $NEW_ACTIVE:8080/g" nginx/nginx.conf

# Nginx 재로드
echo "Nginx 재로드 중..."
docker exec nginx nginx -s reload

echo "5초 대기 후 이전 컨테이너 종료..."
sleep 5

# 이전 컨테이너 종료
echo "[$CURRENT_ACTIVE] 컨테이너 종료 중..."
docker-compose stop $CURRENT_ACTIVE

echo "=== 배포 완료! 활성 컨테이너: $NEW_ACTIVE ==="