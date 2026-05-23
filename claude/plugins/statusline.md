# Claude Code Statusline

Claude Code CLI는 작업 진행 중 다양한 상태 정보를 실시간으로 표시한다. 이 정보들을 잘 읽으면 현재 Claude가 무엇을 하고 있는지, 얼마나 비용이 발생하는지 파악할 수 있다.

## 기본 상태 표시

Claude Code를 실행하면 상단에 기본 정보가 표시된다.

```
╭──────────────────────────────────────────────╮
│ ✻ Welcome to Claude Code!                    │
│                                              │
│   /help for help, /status for your plan      │
╰──────────────────────────────────────────────╯
 ✻ claude-sonnet-4-6  ~/projects/my-app
```

- 사용 중인 **모델명**
- 현재 **작업 디렉토리**

## 도구 실행 상태

Claude가 파일을 읽거나 명령을 실행할 때마다 실행 중인 도구와 대상이 표시된다.

```
● Read(src/auth/login.ts)          ← 파일 읽는 중
● Bash(npm run test)               ← 명령 실행 중
● Edit(src/auth/login.ts)          ← 파일 수정 중
  ⎿  [파일 변경 diff 표시]
```

각 도구 실행은 사용자의 승인이 필요한 경우 확인 메시지가 표시된다.

## 비용 및 토큰 추적

대화가 끝날 때 해당 세션의 토큰 사용량과 비용이 표시된다.

```
Cost: $0.0234  |  Tokens: ↑ 2,456  ↓ 8,123
```

- `↑`: 입력 토큰 (프롬프트 + 컨텍스트)
- `↓`: 출력 토큰 (Claude의 응답)

## /status 명령어

현재 세션의 상태를 확인할 수 있다.

```bash
/status
```

```
Model:     claude-sonnet-4-6
Directory: ~/projects/my-app
Session:   abc123
Cost:      $0.12 (this session)
```

## 권한 모드 표시

Claude Code는 도구 실행 시 권한 수준을 표시한다.

| 표시 | 의미 |
|------|------|
| `⚡ Auto-approved` | 자동 승인 설정된 도구 |
| `? Allow` | 사용자 승인 필요 |
| `✓ Approved` | 승인됨 |
| `✗ Denied` | 거절됨 |

## 활용 팁

- **비용이 예상보다 높다면**: 컨텍스트가 너무 길어진 것이다. `/clear`로 초기화하거나 새 대화를 시작하자.
- **도구 실행이 멈춰보인다면**: 복잡한 작업 중일 수 있다. 잠시 기다리면 진행 상태가 업데이트된다.
- **자주 승인 요청이 뜬다면**: `settings.json`에서 신뢰하는 도구를 자동 승인 목록에 추가하자.
