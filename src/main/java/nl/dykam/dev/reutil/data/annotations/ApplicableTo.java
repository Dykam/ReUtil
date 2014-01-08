package nl.dykam.dev.reutil.data.annotations;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApplicableTo {
    ObjectType[] value() default { /*ObjectType.Block, *//*ObjectType.Chunk, */ObjectType.Player };
}
