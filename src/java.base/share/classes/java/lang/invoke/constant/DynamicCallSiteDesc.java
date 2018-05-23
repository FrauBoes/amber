/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.lang.invoke.constant;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.invoke.constant.ConstantDescs.CR_String;
import static java.lang.invoke.constant.ConstantUtils.EMPTY_CONSTANTDESC;
import static java.lang.invoke.constant.ConstantUtils.validateMemberName;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * A <a href="package-summary.html#nominal">nominal descriptor</a> for an
 * {@code invokedynamic} call site.
 *
 * <p>Concrete subtypes of {@linkplain DynamicCallSiteDesc} must be
 * <a href="../doc-files/ValueBased.html">value-based</a>.
 */
@SuppressWarnings("rawtypes")
public class DynamicCallSiteDesc {

    private final ConstantMethodHandleDesc bootstrapMethod;
    private final ConstantDesc<?>[] bootstrapArgs;
    private final String invocationName;
    private final MethodTypeDesc invocationType;

    /**
     * Create a nominal descriptor for an {@code invokedynamic} call site.
     *
     * @param bootstrapMethod a {@link ConstantMethodHandleDesc} describing the
     *                        bootstrap method for the {@code invokedynamic}
     * @param invocationName The name that would appear in the {@code NameAndType}
     *                       operand of the {@code invokedynamic}, as per
     *                       JVMS 4.2.2
     * @param invocationType a {@link MethodTypeDesc} describing the invocation
     *                       type that would appear in the {@code NameAndType}
     *                       operand of the {@code invokedynamic}
     * @param bootstrapArgs {@link ConstantDesc}s describing the static arguments
     *                      to the bootstrap, that would appear in the
     *                      {@code BootstrapMethods} attribute
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the invocation name has the incorrect
     * format
     */
    private DynamicCallSiteDesc(ConstantMethodHandleDesc bootstrapMethod,
                                String invocationName,
                                MethodTypeDesc invocationType,
                                ConstantDesc<?>[] bootstrapArgs) {
        this.invocationName = validateMemberName(requireNonNull(invocationName));
        this.invocationType = requireNonNull(invocationType);
        this.bootstrapMethod = requireNonNull(bootstrapMethod);
        this.bootstrapArgs = requireNonNull(bootstrapArgs.clone());
        if (invocationName.length() == 0)
            throw new IllegalArgumentException("Illegal invocation name: " + invocationName);
    }

    /**
     * Create a nominal descriptor for an {@code invokedynamic} call site.
     *
     * @param bootstrapMethod a {@link ConstantMethodHandleDesc} describing the
     *                        bootstrap method for the {@code invokedynamic}
     * @param invocationName The name that would appear in the {@code NameAndType}
     *                       operand of the {@code invokedynamic}, as per
     *                       JVMS 4.2.2
     * @param invocationType a {@link MethodTypeDesc} describing the invocation
     *                       type that would appear in the {@code NameAndType}
     *                       operand of the {@code invokedynamic}
     * @param bootstrapArgs {@link ConstantDesc}s describing the static arguments
     *                      to the bootstrap, that would appear in the
     *                      {@code BootstrapMethods} attribute
     * @return the nominal descriptor
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the invocation name has the incorrect
     * format
     */
    public static DynamicCallSiteDesc of(ConstantMethodHandleDesc bootstrapMethod,
                                         String invocationName,
                                         MethodTypeDesc invocationType,
                                         ConstantDesc<?>... bootstrapArgs) {
        return new DynamicCallSiteDesc(bootstrapMethod, invocationName, invocationType, bootstrapArgs);
    }

    /**
     * Create a nominal descriptor for an {@code invokedynamic} call site whose
     * bootstrap method has no static arguments.
     *
     * @param bootstrapMethod The bootstrap method for the {@code invokedynamic}
     * @param invocationName The invocationName that would appear in the
     * {@code NameAndType} operand of the {@code invokedynamic}
     * @param invocationType The invocation invocationType that would appear
     * in the {@code NameAndType} operand of the {@code invokedynamic}
     * @return the nominal descriptor
     * @throws NullPointerException if any parameter is null
     */
    public static DynamicCallSiteDesc of(ConstantMethodHandleDesc bootstrapMethod,
                                         String invocationName,
                                         MethodTypeDesc invocationType) {
        return new DynamicCallSiteDesc(bootstrapMethod, invocationName, invocationType, EMPTY_CONSTANTDESC);
    }

    /**
     * Create a nominal descriptor for an {@code invokedynamic} call site whose
     * bootstrap method has no static arguments and for which the name parameter
     * is {@link ConstantDescs#DEFAULT_NAME}.
     *
     * @param bootstrapMethod a {@link ConstantMethodHandleDesc} describing the
     *                        bootstrap method for the {@code invokedynamic}
     * @param invocationType a {@link MethodTypeDesc} describing the invocation
     *                       type that would appear in the {@code NameAndType}
     *                       operand of the {@code invokedynamic}
     * @return the nominal descriptor
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the invocation name has the incorrect
     * format
     */
    public static DynamicCallSiteDesc of(ConstantMethodHandleDesc bootstrapMethod,
                                         MethodTypeDesc invocationType) {
        return of(bootstrapMethod, ConstantDescs.DEFAULT_NAME, invocationType);
    }

    /**
     * Return a nominal descriptor for an {@code invokedynamic} call site whose
     * bootstrap method, name, and invocation type are the same as this one, but
     * with the specified bootstrap arguments.
     *
     * @param bootstrapArgs {@link ConstantDesc}s describing the static arguments
     *                      to the bootstrap, that would appear in the
     *                      {@code BootstrapMethods} attribute
     * @return the nominal descriptor
     * @throws NullPointerException if any parameter is null
     */
    public DynamicCallSiteDesc withArgs(ConstantDesc<?>... bootstrapArgs) {
        return new DynamicCallSiteDesc(bootstrapMethod, invocationName, invocationType, bootstrapArgs);
    }

    /**
     * Return a nominal descriptor for an {@code invokedynamic} call site whose
     * bootstrap and bootstrap arguments are the same as this one, but with the
     * specified invocationName and invocation invocationType
     *
     * @param invocationName The name that would appear in the {@code NameAndType}
     *                       operand of the {@code invokedynamic}, as per
     *                       JVMS 4.2.2
     * @param invocationType a {@link MethodTypeDesc} describing the invocation
     *                       type that would appear in the {@code NameAndType}
     *                       operand of the {@code invokedynamic}
     * @return the nominal descriptor
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if the invocation name has the incorrect
     * format
     */
    public DynamicCallSiteDesc withNameAndType(String invocationName,
                                               MethodTypeDesc invocationType) {
        return new DynamicCallSiteDesc(bootstrapMethod, invocationName, invocationType, bootstrapArgs);
    }

    private DynamicCallSiteDesc canonicalize() {
        // @@@ MethodDesc
        return this;
    }

    /**
     * Returns the invocation name that would appear in the {@code NameAndType}
     * operand of the {@code invokedynamic}.
     * \
     * @return the invocation name
     */
    public String invocationName() {
        return invocationName;
    }

    /**
     * Returns a {@link MethodTypeDesc} describing the invocation type that
     * would appear in the {@code NameAndType} operand of the {@code invokedynamic}.
     *
     * @return the invocation type
     */
    public MethodTypeDesc invocationType() {
        return invocationType;
    }

    /**
     * Returns a {@link MethodHandleDesc} descripbing the bootstrap method for
     * the {@code invokedynamic}.
     *
     * @return the bootstrap method for the {@code invokedynamic}
     */
    public MethodHandleDesc bootstrapMethod() { return bootstrapMethod; }

    /**
     * Returns {@link ConstantDesc}s describing the bootstrap arguments for the
     * {@code invokedynamic}.
     *
     * @return the bootstrap arguments for the {@code invokedynamic}
     */
    public ConstantDesc<?>[] bootstrapArgs() { return bootstrapArgs.clone(); }

    /**
     * Reflectively invokes the bootstrap method with the specified arguments,
     * and return the resulting {@link CallSite}
     *
     * @param lookup The {@link MethodHandles.Lookup} used to resolve class names
     * @return the {@link CallSite}
     * @throws Throwable if any exception is thrown by the bootstrap method
     */
    public CallSite resolveCallSiteDesc(MethodHandles.Lookup lookup) throws Throwable {
        assert bootstrapMethod.methodType().parameterType(1).equals(CR_String);
        MethodHandle bsm = bootstrapMethod.resolveConstantDesc(lookup);
        Object[] args = new Object[bootstrapArgs.length + 3];
        args[0] = lookup;
        args[1] = invocationName;
        args[2] = invocationType.resolveConstantDesc(lookup);
        System.arraycopy(bootstrapArgs, 0, args, 3, bootstrapArgs.length);
        return (CallSite) bsm.invokeWithArguments(args);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicCallSiteDesc specifier = (DynamicCallSiteDesc) o;
        return Objects.equals(bootstrapMethod, specifier.bootstrapMethod) &&
               Arrays.equals(bootstrapArgs, specifier.bootstrapArgs) &&
               Objects.equals(invocationName, specifier.invocationName) &&
               Objects.equals(invocationType, specifier.invocationType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(bootstrapMethod, invocationName, invocationType);
        result = 31 * result + Arrays.hashCode(bootstrapArgs);
        return result;
    }

    @Override
    public String toString() {
        return String.format("DynamicCallSiteDesc[%s::%s(%s%s):%s]",
                             bootstrapMethod.owner().displayName(),
                             bootstrapMethod.methodName(),
                             invocationName.equals(ConstantDescs.DEFAULT_NAME) ? "" : invocationName + "/",
                             Stream.of(bootstrapArgs).map(Object::toString).collect(joining(",")),
                             invocationType.displayDescriptor());
    }
}
