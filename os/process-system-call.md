## OS - Process의 생성과 제어를 위한 System Calls!

Process의 생성과 제어를 위한 4가지 SystemCall에 대해 알아보자.
- Fork
- Wait
- Exec
- Exit

## Fork

새로운 Process를 생성한다.

```C
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char *argv[]) {
    printf("pid : %d", (int) getpid()); // pid : 29146
    
    int rc = fork();					// 주목
    
    if (rc < 0) {
        exit(1);
    }									// (1) fork 실패
    else if (rc == 0) {					// (2) child 인 경우 (fork 값이 0)
        printf("child (pid : %d)", (int) getpid());
    }
    else {								// (3) parent case
        printf("parent of %d (pid : %d)", rc, (int)getpid());
    }
}
```

> pid: 29147
> 
> parent of 29147 (pid : 29146)
> 
> child (pid : 29147)

새롭게 생성되는 자식 프로세스는 새로운 PID를 갖게되어 호출한 부모 프로세스를 그대로 복사한다. 복사를 통해 자식 프로세스는 부모와 완전히 독립된 물리 메모리 공간을 갖는다.

(단, 파일 디스크립터는 동작이 다르다.)
- 자식 프로세스는 부모 파일 디스크립터의 복사본을 갖는다. 이 디스크립터는 같은 오브젝트를 참조하므로 후속 읽기 또는 쓰기 영향을 미칠 수 있다.

## Wait

child 프로세스가 종료될 때까지 기다린다.

```C
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main(int argc, char *argv[]) {
    printf("pid : %d", (int) getpid()); // pid : 29146
    
    int rc = fork();					// 주목
    
    if (rc < 0) {
        exit(1);
    }									// (1) fork 실패
    else if (rc == 0) {					// (2) child 인 경우 (fork 값이 0)
        printf("child (pid : %d)", (int) getpid());
    }
    else {								// (3) parent case
        int wc = wait(NULL)				// 추가된 부분
        printf("parent of %d (wc : %d / pid : %d)", wc, rc, (int)getpid());
    }
}
```
> pid: 29146
>
> child (pid : 29147)
>
> parent of 29147 (wc: 29147 / pid : 29146)

parent가 먼저 실행되더라도 wait()를 사용해서 child가 끝나기 전에는 return 하지 않으므로, 반드시 child가 먼저 종료된다.

자식 프로세스가 고아 프로세스가 되는 것을 방지한다.

## Exec

단순 fork는 동일한 프로세스의 내용을 여러 번 동작할 때 사용한다.

child에서는 parent와 다른 동작을 하고 싶을 때는 exec을 사용할 수 있다.

```C
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main(int argc, char *argv[]) {
    printf("pid : %d", (int) getpid()); // pid : 29146
    
    int rc = fork();					// 주목
    
    if (rc < 0) {
        exit(1);
    }									// (1) fork 실패
    else if (rc == 0) {					// (2) child 인 경우 (fork 값이 0)
        printf("child (pid : %d)", (int) getpid());
        char *myargs[3];
        myargs[0] = strdup("wc");		// 내가 실행할 파일 이름
        myargs[1] = strdup("p3.c");		// 실행할 파일에 넘겨줄 argument
        myargs[2] = NULL;				// end of array
        execvp(myarges[0], myargs);		// wc 파일 실행.
        printf("this shouldn't print out") // 실행되지 않음.
    }
    else {								// (3) parent case
        int wc = wait(NULL)				// 추가된 부분
        printf("parent of %d (wc : %d / pid : %d)", wc, rc, (int)getpid());
    }
}
```

## Exit
exit 함수를 호출하면 어떤 종료 코드를 사용하던 **정상 종료**가 된다.
```C
exit(1)
```

## 참고
- https://github.com/gyoogle/tech-interview-for-developer/blob/master/Computer%20Science/Operating%20System/%5BOS%5D%20System%20Call%20(Fork%20Wait%20Exec).md
