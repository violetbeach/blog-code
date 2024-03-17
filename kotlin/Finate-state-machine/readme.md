suspend의 내부 구현을 이해하려면 Kotlin compiler과 Finite state machine, CPS(Continuation passing style)를 알아야 한다.

#### Finite state machine

State Machine이란 시스템이 가질 수 있는 상태를 표현하는 추상적인 모델이다. 아래는 컴포넌트 들이다.
- State: 시스템의 특정 상황
- Transition: 하나의 State에서 다른 State로 이동
- Event: Transition을 trigger하는 외부의 사건

즉, 1개의 시스템은 특정 State에 있고, Event가 발생되면 Transition을 통해 다른 Status로 변경될 수 있다.

Finite는 유한의 의미이고, Finate state machine은 유한한 State를 갖는 모델이다.