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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;

import static java.lang.invoke.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.invoke.constant.ConstantDescs.CR_MethodHandle;
import static java.util.Objects.requireNonNull;

/**
 * A <a href="package-summary.html#nominal">nominal descriptor</a> for a
 * {@link MethodHandle} constant that performs a {@link MethodHandle#asType(MethodType)}
 * adaptation on another {@link MethodHandle}.
 */
final class AsTypeMethodHandleDesc extends DynamicConstantDesc<MethodHandle>
        implements MethodHandleDesc {

    private final MethodHandleDesc underlying;
    private final MethodTypeDesc type;

    AsTypeMethodHandleDesc(MethodHandleDesc underlying, MethodTypeDesc type) {
        super(BSM_INVOKE, ConstantDescs.DEFAULT_NAME, CR_MethodHandle,
              ConstantDescs.MHR_METHODHANDLE_ASTYPE, underlying, type);
        this.underlying = requireNonNull(underlying);
        this.type = requireNonNull(type);
    }

    @Override
    public MethodTypeDesc methodType() {
        return type;
    }

    @Override
    public MethodHandle resolveConstantDesc(MethodHandles.Lookup lookup)
            throws ReflectiveOperationException {
        MethodHandle handle = underlying.resolveConstantDesc(lookup);
        MethodType methodType = type.resolveConstantDesc(lookup);
        return handle.asType(methodType);
    }

    @Override
    public Optional<? extends ConstantDesc<ConstantDesc<MethodHandle>>> describeConstable() {
        return ConstantUtils.symbolizeHelper(ConstantDescs.MHR_METHODHANDLEDESC_ASTYPE, ConstantDescs.CR_MethodHandleDesc,
                                             underlying, type);
    }

    @Override
    public String toString() {
        return  String.format("%s.asType%s", underlying.toString(), type.displayDescriptor());
    }
}
