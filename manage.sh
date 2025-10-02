#!/bin/bash

# Dừng script ngay lập tức nếu có lệnh nào đó thất bại
set -e

# --- Màu sắc để log cho đẹp ---
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# --- Các hàm chức năng ---

# Chạy ứng dụng ở chế độ development
run_dev() {
    echo -e "${GREEN}>>> Starting Spring Boot application...${NC}"
    ./gradlew bootRun
}

# Build ứng dụng ra file JAR để triển khai (deploy)
build_prod() {
    echo -e "${GREEN}>>> Building application for PRODUCTION...${NC}"
    echo -e "${YELLOW}Running Gradle build (skipping tests for speed)...${NC}"
    ./gradlew build -x test
    echo -e "${GREEN}>>> Build complete!${NC}"
    echo -e "Executable JAR file is located at: ${YELLOW}build/libs/ecommerce-0.0.1-SNAPSHOT.jar${NC}"
}

# Cài đặt hoặc cập nhật tất cả các thư viện
update_libs() {
    echo -e "${GREEN}>>> Updating/installing dependencies...${NC}"
    # Lệnh build sẽ tự động tải về các dependencies còn thiếu
    ./gradlew build --refresh-dependencies -x test
    echo -e "${GREEN}>>> Dependencies are up to date.${NC}"
}

# Dọn dẹp project
clean_project() {
    echo -e "${GREEN}>>> Cleaning the project...${NC}"
    ./gradlew clean
    echo -e "${GREEN}>>> Project cleaned.${NC}"
}

# --- Điểm bắt đầu của script ---
COMMAND=$1

if [ -z "$COMMAND" ]; then
    echo -e "${RED}Error: Command not specified.${NC}"
    echo "Usage: ./manage.sh [command]"
    echo "Available commands:"
    echo -e "  ${YELLOW}dev${NC}    - Starts the application."
    echo -e "  ${YELLOW}build${NC}  - Builds a production-ready JAR file."
    echo -e "  ${YELLOW}update${NC} - Installs or updates all dependencies."
    echo -e "  ${YELLOW}clean${NC}  - Cleans the build directory."
    exit 1
fi

case $COMMAND in
    dev)
        run_dev
        ;;
    build)
        build_prod
        ;;
    update)
        update_libs
        ;;
    clean)
        clean_project
        ;;
    *)
        echo -e "${RED}Error: Unknown command '$COMMAND'${NC}"
        exit 1
        ;;
esac