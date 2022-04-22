package TemplateMethodPattern;

public abstract class Super {
    public void templateMethod(){
        //기본 알고리즘 코드
        hookMethod();
        abstractMethod();
    }
    // 선택적으로 오버라이드 가능한 훅 메소드
    protected void hookMethod() {}

    // 서브클래스에서 반드시 구현해야되는 추상 클래스
    public abstract void abstractMethod();
}
