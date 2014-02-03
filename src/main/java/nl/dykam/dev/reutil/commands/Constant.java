package nl.dykam.dev.reutil.commands;

public @interface Constant {
    String[] s() default {};
    int[] i() default {};
    float[] f() default {};
    double[] d() default {};
    boolean[] b() default {};
}
