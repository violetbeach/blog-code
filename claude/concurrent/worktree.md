# Claude - Worktree로 병렬 개발하기

## Git Worktree

하나의 저장소에서 여러 개의 작업 디렉토리를 동시에 관리할 수 있는 기능입니다. 브랜치 전환 시 기존 작업을 stash하거나 커밋해야 하는 번거로움을 해결할 수 있습니다.

## Worktree의 장점

1. **병렬 작업**: 여러 기능을 동시에 개발 가능 (예: feature/auth와 bugfix/login 동시 작업)
2. **빠른 브랜치 전환**: 물리적으로 다른 디렉토리에 있어 instant switching 가능
3. **깔끔한 작업 공간**: 각 작업마다 독립된 디렉토리 유지로 혼동 최소화
4. **컨텍스트 보존**: 각 worktree는 독립된 상태 유지

## 주요 명령어

```
git worktree add ../project-feature-auth feature/auth
git worktree add -b feature/auth ../project-feature-auth
git worktree list
git worktree remove ../project-feature-auth
git worktree prune
```

## Worktree vs Branch Switching

| 측면 | Worktree | Branch Switching |
|------|----------|------------------|
| 컨텍스트 유지 | ✅ 완전 유지 | ❌ stash 필요 |
| 동시 작업 | ✅ 가능 | ❌ 불가능 |
| 디스크 사용 | 추가 사용 | ✅ 동일 |
| 복잡도 | 다소 높음 | ✅ 낮음 |

## Claude Code + Worktree

`--worktree` 플래그로 브랜치 생성, worktree 설정, Claude 실행을 한 번에 처리합니다:

```
claude --worktree feature/payment
```

### node_modules 중복 방지

`.claude/settings.json`에 symlink 설정:

```json
{
  "worktree": {
    "symlinkDirectories": ["node_modules", ".cache", ".venv"]
  }
}
```

## IntelliJ + Worktree

IDE 검색 시 해당 worktree 클래스도 함께 검색될 수 있으므로, **Mark Directory as Excluded**로 검색 대상에서 제외하거나 별도 경로에 worktree를 생성합니다.

## 유용한 상황

- 긴급한 버그 수정이 필요할 때
- 여러 기능 브랜치를 동시에 작업할 때
- 코드 리뷰를 위해 특정 브랜치를 별도 환경에서 확인할 때
- 실험적인 변경을 메인 작업과 분리해서 진행할 때

## 참고

- [Git 공식 문서 - git worktree](https://git-scm.com/docs/git-worktree)
- [Claude Code 공식 문서 - Worktree](https://docs.anthropic.com/en/docs/claude-code/worktrees)
