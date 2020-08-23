/*
 * Copyright (C) 2016, 2018 Player, asie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.fabricmc.tinyremapper;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

public class BridgeFixer_Post extends ClassVisitor {

    public BridgeFixer_Post(ClassVisitor visitor) {
        super(Opcodes.ASM8, visitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (descriptor.startsWith(BridgeFixer_Pre.PREFIX)) {
            descriptor = Type.getMethodDescriptor(
                    Type.getReturnType(descriptor),
                    Arrays.stream(Type.getArgumentTypes(descriptor))
                            .skip(2)
                            .toArray(Type[]::new));
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
