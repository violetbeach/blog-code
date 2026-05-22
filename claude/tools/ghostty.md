# Ghostty: GPU 가속 터미널

Ghostty는 GPU 가속 터미널 에뮬레이터다. 기존의 터미널들에 비해 렌더링 성능이 뛰어나고, 현대적인 사용 경험을 제공한다.

## Ghostty의 특징

1. **GPU 가속 렌더링**: CPU 대신 GPU를 활용해 대량의 텍스트 출력과 스크롤이 매우 빠르다.
2. **네이티브 앱**: macOS에서는 SwiftUI 기반으로 만들어져 시스템 통합이 자연스럽다.
3. **크로스 플랫폼**: macOS와 Linux를 지원한다.
4. **풍부한 설정**: 색상 테마, 폰트, 키바인딩 등을 자유롭게 커스터마이징할 수 있다.

## 설치 방법

### macOS
```bash
brew install --cask ghostty
```

### Linux
패키지 매니저가 아닌 소스 빌드가 기본이다. 일부 배포판에서는 커뮤니티 패키지를 통해 설치할 수 있다.

```bash
# NixOS
nix-env -iA nixpkgs.ghostty

# AUR (Arch Linux)
yay -S ghostty-bin
```

## 설정 파일

설정 파일 위치: `~/.config/ghostty/config`

Claude Code와 함께 사용하기 좋은 설정:

```
font-family = JetBrains Mono
font-size = 14
background = #1e1e2e
foreground = #cdd6f4
window-padding-x = 8
window-padding-y = 8

# 탭 바 항상 표시 (여러 세션 관리 시 유용)
window-tab-bar-always = true
```

## Claude Code와 함께 쓰는 이유

Claude Code는 터미널에서 실행되는 CLI 도구다. Ghostty의 빠른 렌더링은 Claude Code를 사용할 때 체감 성능 차이를 만든다.

- **대량 출력 처리**: Claude Code가 코드를 생성하거나 파일을 수정할 때 대량의 텍스트가 출력된다. GPU 가속 덕분에 끊김 없이 렌더링된다.
- **여러 세션 동시 관리**: 탭과 분할 창으로 여러 worktree에서 동시에 Claude Code를 실행할 수 있다.
- **빠른 스크롤**: 긴 로그나 에러 메시지를 빠르게 스크롤해서 확인할 수 있다.

## Worktree 병렬 개발 환경 구성

Ghostty의 분할 창 기능을 활용하면 여러 worktree에서 Claude Code를 동시에 실행하는 환경을 쉽게 만들 수 있다.

```
┌─────────────────────┬─────────────────────┐
│ ../hotfix-security  │ ../feature-dashboard │
│                     │                      │
│ $ claude            │ $ claude             │
│ > 보안 패치 작업 중  │ > 대시보드 개발 중   │
│                     │                      │
└─────────────────────┴─────────────────────┘
```

기본 단축키 (macOS 기준):

- `Cmd+D`: 세로 분할
- `Cmd+Shift+D`: 가로 분할
- `Cmd+T`: 새 탭
- `Cmd+[` / `Cmd+]`: 분할 창 이동

## 성능 비교

| 터미널 | 렌더링 방식 | 특징 |
|--------|-------------|------|
| Terminal.app | CPU | macOS 기본 포함, 가벼움 |
| iTerm2 | CPU/Metal | 풍부한 기능, macOS 전용 |
| Ghostty | GPU | 빠른 렌더링, 크로스 플랫폼 |
| WezTerm | GPU | 고도의 커스터마이징 가능 |

대량의 텍스트 처리나 여러 터미널을 동시에 사용하는 환경에서 Ghostty의 장점이 두드러진다.
