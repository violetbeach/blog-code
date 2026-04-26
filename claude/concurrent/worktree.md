# Claude - Worktree로 병렬 개발하기

## Git Worktree

Git worktree는 하나의 저장소에서 여러 개의 작업 디렉토리를 동시에 관리할 수 있는 기능이다. 일반적으로 브랜치를 전환할 때 기존 작업을 stash하거나 커밋해야 하는 번거로움이 있는데, worktree를 사용하면 이를 해결할 수 있다.

## Worktree의 장점

1. **병렬 작업**: 여러 기능을 동시에 개발할 수 있다. 예를 들어 feature/auth 브랜치와 bugfix/login 브랜치를 동시에 작업할 수 있다.
2. **빠른 브랜치 전환**: 물리적으로 다른 디렉토리에 있으므로 instant switching이 가능하다.
3. **깔끔한 작업 공간**: 각 작업마다 독립된 디렉토리를 유지하므로 혼동이 적다.
4. **컨텍스트 보존**: 각 worktree는 독립된 상태를 유지하므로 작업 중인 내용이 섞이지 않는다.

## 주요 명령어

```bash
# 새로운 worktree 생성 (브랜치가 이미 존재하는 경우)
git worktree add ../project-feature-auth feature/auth

# 새로운 worktree 생성 + 브랜치 동시 생성
git worktree add -b feature/auth ../project-feature-auth

# 기존 worktree 목록 조회
git worktree list

# worktree 삭제
git worktree remove ../project-feature-auth

# 정리 (이미 삭제된 디렉토리 참조 제거)
git worktree prune
```

## 예시

### 시나리오: 긴급 버그 수정 + 신기능 개발

```bash
# 긴급 버그 수정용 worktree 생성
git worktree add -b hotfix/login-fix ../hotfix-login

# 신기능 개발용 worktree 생성
git worktree add -b feature/payment-gateway ../feature-payment

# 터미널 1: 버그 수정
cd ../hotfix-login
# 버그 수정 작업...
git commit -m "fix: 로그인 버그 수정"

# 터미널 2: 신기능 개발 (동시에)
cd ../feature-payment
# 신기능 개발 작업...
```

두 가지 작업을 동시에 진행하면서도 각 작업의 컨텍스트를 완전히 분리할 수 있다.

## Worktree vs Branch Switching

| 측면 | Worktree | Branch Switching |
|------|----------|------------------|
| 컨텍스트 유지 | ✅ 완전 유지 | ❌ stash 필요 |
| 동시 작업 | ✅ 가능 | ❌ 불가능 |
| 디스크 사용 | 추가 사용 | ✅ 동일 |
| 복잡도 | 다소 높음 | ✅ 낮음 |

## 정리

Worktree는 특히 다음과 같은 상황에서 유용하다:

- 긴급한 버그 수정이 필요할 때
- 여러 기능 브랜치를 동시에 작업할 때
- 코드 리뷰를 위해 특정 브랜치를 별도 환경에서 확인할 때
- 실험적인 변경을 메인 작업과 분리해서 진행할 때

---

## Claude Code + Worktree

동일 Repository 내에서 여러 작업을 Claude Code를 활용해서 동시에 진행해야 하는 경우가 있다. Claude Code는 `--worktree` 플래그를 통해 worktree 생성과 Claude 실행을 한 번에 처리한다.

```bash
claude --worktree feature/payment
```

이 명령어 하나로 브랜치 생성, git worktree 설정, 해당 worktree에서 Claude Code 실행까지 자동으로 처리된다. 수동으로 `git worktree add` 하고 `cd`해서 `claude` 실행할 필요가 없다.

### 실전 워크플로우

```bash
# 터미널 1: 긴급 버그 수정
claude --worktree hotfix/security-patch

# 터미널 2: 신기능 개발 (동시에)
claude --worktree feature/user-dashboard
```

```
터미널 1                          터미널 2
┌─────────────────────────┐       ┌─────────────────────────┐
│ claude --worktree        │       │ claude --worktree        │
│   hotfix/security-patch  │       │   feature/user-dashboard │
│                          │       │                          │
│ > 보안 취약점 수정해줘    │       │ > 대시보드 컴포넌트 만들어 │
└─────────────────────────┘       └─────────────────────────┘
```

각 Claude 인스턴스는 자신의 worktree 브랜치와 파일 구조만 인식하므로, 서로 간섭 없이 완전히 독립적으로 작동한다.

### node_modules 중복 방지

`--worktree`로 새 worktree를 만들면 `node_modules` 같은 대용량 디렉토리가 복사되어 디스크를 낭비할 수 있다. `CLAUDE.md` 또는 `.claude/settings.json`에 symlink할 디렉토리를 지정하면 된다.

```json
{
  "worktree": {
    "symlinkDirectories": ["node_modules", ".cache", ".venv"]
  }
}
```

이렇게 설정하면 worktree를 만들 때 해당 디렉토리는 복사하지 않고 메인 레포지토리의 것을 그대로 참조한다.

### 효과적인 활용 패턴

**패턴 1: 버그 수정 + 기능 개발 동시 진행**

**패턴 2: 대규모 리팩토링 + 일반 개발 병행**

**패턴 3: 여러 기능을 독립적으로 개발**

---

## IntelliJ + Worktree

Worktree를 사용하면 IntelliJ와 같은 IDE에서 검색 시 해당 Worktree의 클래스들도 같이 검색이 되기에 불편할 수 있다.

그래서 Worktree는 별도의 경로에 생성하거나, IDE에서 **Mark Directory as Excluded**로 검색 및 인덱싱 대상에서 제외한다.

해당 Worktree의 내용을 IDE에서 보고싶을 때는 IntelliJ에서 해당 worktree 폴더를 별도로 열어서 작업할 수 있다.

---

## 참고

- [Claude Code 공식 문서 - Worktree](https://docs.anthropic.com/en/docs/claude-code/worktrees)
