# KHUREKA Server 기여 가이드 (Contributing Guide)

이 문서는 KHUREKA 백엔드 서버 프로젝트의 로컬 환경 세팅 및 개발 워크플로우에 대해 설명합니다.

## 🚀 시작하기 (Getting Started)

### 사전 요구사항 (Prerequisites)
- **Java**: 17 버전을 사용합니다.
- **Docker**: 로컬 DB(MySQL) 실행을 위해 필요합니다.
- **Git**: 소스 코드 관리를 위해 필요합니다.

### 환경 세팅 및 실행
1. **저장소 클론**
   ```bash
   git clone https://github.com/KHUREKA/back.git
   cd back
   ```

2. **환경 변수 파일 세팅**
   루트 디렉토리에 있는 `.env.example` 파일을 복사하여 `.env` 파일을 생성합니다.
   ```bash
   cp .env.example .env
   ```
   이후 `.env` 파일을 열어 본인의 로컬 환경에 맞게 `MYSQL_USER`, `MYSQL_PASSWORD`, `JWT_SECRET` 등을 설정합니다. (보안상 실제 `.env` 파일은 깃에 올라가지 않습니다.)

3. **로컬 DB 컨테이너 실행**
   설정된 환경 변수들을 기반으로 Docker 컨테이너를 실행합니다.
   ```bash
   docker-compose up -d
   ```

4. **서버 실행**
   IDE에서 `ServerApplication`을 실행하거나 터미널에서 아래 명령어로 서버를 구동합니다.
   ```bash
   ./gradlew bootRun
   ```

5. **Swagger 접속 확인**
   서버가 구동되면 `http://localhost:8080/swagger-ui/index.html`에 접속하여 API 문서를 확인합니다.

---

## 🌿 브랜치 전략 및 커밋 컨벤션 (Workflow)

본 프로젝트는 안정적인 협업을 위해 아래의 브랜치 네이밍 및 커밋 컨벤션을 따릅니다.

### 1. 브랜치 네이밍 규칙
새로운 기능을 개발하거나 버그를 수정할 때는 **반드시 새로운 브랜치를 생성**하여 작업합니다. `main` 브랜치로의 직접적인 푸시는 지양합니다.
- `feat/기능명`: 새로운 기능 추가 (예: `feat/login`)
- `fix/버그명`: 버그 수정 (예: `fix/jwt-error`)
- `docs/문서명`: 문서 작업 (예: `docs/readme-update`)
- `refactor/작업명`: 코드 리팩토링 (비즈니스 로직 변화 없음)

**브랜치 생성 예시**:
```bash
git checkout -b feat/user-signup
```

### 2. 커밋 메시지 컨벤션
명확한 히스토리 관리를 위해 커밋 타입 접두사를 사용합니다. 첫 줄에 타입과 작업 내용을 영문 또는 한글로 명시해 주세요.
- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 포맷팅, 세미콜론 누락 등 (코드 변경 없음)
- `refactor`: 코드 리팩토링
- `test`: 테스트 코드 추가
- `chore`: 빌드 설정, 환경 설정 변경 (.gitignore, build.gradle, .env 등)

**커밋 메시지 예시**:
```text
feat: add JWT authentication filter
fix: resolve database connection issue in docker
docs: add contribution guide
```

### 3. 작업 완료 및 코드 반영 (Pull Request)
작업이 완료되면 원격 저장소에 푸시한 후 `main` 브랜치로 Pull Request(PR)를 생성합니다.
```bash
git add .
git commit -m "feat: 완료한 기능에 대한 설명"
git push origin feat/user-signup
```
- PR 제목은 커밋 메시지와 동일한 컨벤션(`타입: 내용`)을 사용해 주세요.
- 변경된 코드가 잘 작동하는지 로컬에서 충분히 테스트한 뒤 올립니다.
- 팀원의 코드 리뷰(Approve)를 받은 후 Merge를 진행합니다.
