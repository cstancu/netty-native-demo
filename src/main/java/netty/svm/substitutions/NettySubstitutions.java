package netty.svm.substitutions;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;
import com.oracle.svm.core.annotate.TargetClass;

import java.util.function.Predicate;

@TargetClass(className = "io.netty.buffer.AbstractReferenceCountedByteBuf", onlyWith = PlatformHasClass.class)
final class Target_io_netty_buffer_AbstractReferenceCountedByteBuf {
    @Alias
    @RecomputeFieldValue(kind = Kind.FieldOffset, //
            declClassName = "io.netty.buffer.AbstractReferenceCountedByteBuf", //
            name = "refCnt") //
    private static long REFCNT_FIELD_OFFSET;
}

@TargetClass(className = "io.netty.util.AbstractReferenceCounted", onlyWith = PlatformHasClass.class)
final class Target_io_netty_util_AbstractReferenceCounted {
    @Alias
    @RecomputeFieldValue(kind = Kind.FieldOffset, //
            declClassName = "io.netty.util.AbstractReferenceCounted", //
            name = "refCnt") //
    private static long REFCNT_FIELD_OFFSET;
}

/**
 * A predicate to tell whether this platform includes the argument class.
 */
final class PlatformHasClass implements Predicate<String> {
    @Override
    public boolean test(String className) {
        try {
            @SuppressWarnings({ "unused" })
            final Class<?> classForName = Class.forName(className);
            return true;
        } catch (ClassNotFoundException cnfe) {
            return false;
        }
    }
}

public class NettySubstitutions {
}
