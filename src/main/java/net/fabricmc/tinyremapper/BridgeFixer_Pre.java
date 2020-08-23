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
import org.objectweb.asm.tree.MethodNode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class BridgeFixer_Pre extends ClassVisitor {

    private static final int SYNTHETIC_BRIDGE = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE;
    private final TinyRemapper remapper;

    static final String PREFIX = "(Loh god oh fuck please help us god. And now, for something completely random. " + Math.random() + ";L";

    public BridgeFixer_Pre(ClassVisitor visitor, TinyRemapper remapper) {
        super(Opcodes.ASM8, visitor);
        this.remapper = remapper;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ((access & SYNTHETIC_BRIDGE) == SYNTHETIC_BRIDGE) {
            return new PreFix(access, name, descriptor, signature, exceptions);
        } else {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private class PreFix extends MethodNode {
        private String newDesc;

        public PreFix(int access, String name, String descriptor, String signature, String[] exceptions) {
            super(BridgeFixer_Pre.this.api, access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);

            ClassInstance classInstance = remapper.classes.get(owner);

            if (classInstance != null) {
                MemberInstance member = classInstance.getMember(MemberInstance.MemberType.METHOD, MemberInstance.getMethodId(name, descriptor));

                if (member != null) {
                    this.newDesc = member.desc;
                    this.signature = member.signature;
                }
            }
        }

        @Override
        public void visitEnd() {
            String d = PREFIX + encode(desc) + ";" + newDesc.substring(1);
            accept(visitMethod(access & ~SYNTHETIC_BRIDGE, name, d, signature, exceptions.toArray(new String[0])));
        }
    }

    static String encode(String s) {
        return new String(Base64.getEncoder().encode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    static String decode(String s) {
        return new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
