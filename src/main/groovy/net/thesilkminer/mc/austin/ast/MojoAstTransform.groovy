/*
 * Copyright (c) 2023 TheSilkMiner
 * SPDX-License-Identifier: MIT
 */

package net.thesilkminer.mc.austin.ast

import groovy.transform.CompileStatic
import groovy.transform.Generated
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.thesilkminer.mc.austin.MojoContainer
import net.thesilkminer.mc.austin.api.EventBus
import net.thesilkminer.mc.austin.api.GMod
import net.thesilkminer.mc.austin.api.Mod
import net.thesilkminer.mc.austin.api.Mojo
import net.thesilkminer.mc.austin.api.MojoEventBus
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
@SuppressWarnings('unused')
final class MojoAstTransform extends AbstractASTTransformation {

    private static final ClassNode TARGET_ANNOTATION = ClassHelper.make(Mojo)
    private static final ClassNode ALTERNATIVE_TARGET_ANNOTATION = ClassHelper.make(Mod)
    private static final ClassNode ALTERNATIVE_TARGET_ANNOTATION_2 = ClassHelper.make(GMod)

    private static final AnnotationNode GENERATED_ANNOTATION_NODE = new AnnotationNode(ClassHelper.make(Generated))

    private static final ClassNode BUS_ENUM = ClassHelper.make(EventBus)
    private static final ClassNode EVENT_BUS_INTERFACE = ClassHelper.make(IEventBus)
    private static final ClassNode MOJO_EVENT_BUS = ClassHelper.make(MojoEventBus)
    private static final ClassNode MINECRAFT_FORGE = ClassHelper.make(MinecraftForge)
    private static final ClassNode MOJO_CONTAINER = ClassHelper.make(MojoContainer)

    @SuppressWarnings('SpellCheckingInspection')
    private static final String MOJO_CONTAINER_NAME = '$$aplp$synthetic$mojoContainer$$'
    private static final String MOJO_BUS = 'mojoBus'
    private static final String TO_STRING = 'toString'

    @Override
    void visit(final ASTNode[] nodes, final SourceUnit source) {
        this.init(nodes, source)

        final AnnotationNode annotation = nodes[0] as AnnotationNode
        final AnnotatedNode node = nodes[1] as AnnotatedNode

        if (annotation.classNode !in [TARGET_ANNOTATION, ALTERNATIVE_TARGET_ANNOTATION, ALTERNATIVE_TARGET_ANNOTATION_2])
            return

        if (node instanceof ClassNode) {
            generateMethods(node)
        }
    }

    private static void generateMethods(final ClassNode node) {
        fixConstructor(node)
        generateContainerField(node)
        generateMojoBusGetter(node)
        generateForgeBusGetter(node)
        generateModBusGetter(node)
        generateToString(node)
    }

    private static void fixConstructor(final ClassNode node) {
        final ConstructorNode noArgConstructor = node.declaredConstructors.find {it.parameters.size() === 0 }
        node.declaredConstructors.remove(noArgConstructor)
        node.addConstructor(generateNewConstructor(node, noArgConstructor))
    }

    private static ConstructorNode generateNewConstructor(final ClassNode owner, final ConstructorNode previous) {
        final int modifiers = previous.modifiers //| 0x1000 // ACC_SYNTHETIC
        final Parameter[] parameters = [new Parameter(MOJO_CONTAINER, MOJO_CONTAINER_NAME)]
        final Statement code = GeneralUtils.block(
                GeneralUtils.assignS(GeneralUtils.thisPropX(false, MOJO_CONTAINER_NAME), GeneralUtils.varX(MOJO_CONTAINER_NAME)),
                previous.code
        )
        final ConstructorNode node = new ConstructorNode(modifiers, parameters, ClassNode.EMPTY_ARRAY, code)
        node.addAnnotation(GENERATED_ANNOTATION_NODE)
        return node
    }

    private static Expression expressionFromBus(final EventBus bus) {
        return switch (bus) {
            case EventBus.FORGE -> GeneralUtils.propX(GeneralUtils.classX(MINECRAFT_FORGE), 'EVENT_BUS')
            case EventBus.MOJO, EventBus.MOD -> GeneralUtils.propX(GeneralUtils.thisPropX(false, MOJO_CONTAINER_NAME), MOJO_BUS)
        }
    }

    private static void generateContainerField(final ClassNode owner) {
        if (owner.getDeclaredField(MOJO_CONTAINER_NAME)) return

        final FieldNode node = owner.addField(MOJO_CONTAINER_NAME, 0x112 | 0x1000, MOJO_CONTAINER, null)
        node.addAnnotation(GENERATED_ANNOTATION_NODE)
    }

    private static void generateMojoBusGetter(final ClassNode owner) {
        generateProperty(owner, MOJO_BUS, expressionFromBus(EventBus.MOJO), MOJO_EVENT_BUS)
    }

    private static void generateForgeBusGetter(final ClassNode owner) {
        generateProperty(owner, 'forgeBus', expressionFromBus(EventBus.FORGE))
    }

    private static void generateModBusGetter(final ClassNode owner) {
        // redirect to this.mojoBus
        generateProperty(owner, 'modBus', GeneralUtils.thisPropX(false, MOJO_BUS), MOJO_EVENT_BUS)
    }

    private static void generateProperty(final ClassNode owner, final String name, final Expression getter, final ClassNode returnType = EVENT_BUS_INTERFACE) {
        if (owner.getProperty(name) || owner.getDeclaredField(name)) return

        final String methodName = "get${name.capitalize()}"

        if (owner.getDeclaredMethod(methodName, Parameter.EMPTY_ARRAY)) return

        final Statement getterCode = GeneralUtils.block(GeneralUtils.returnS(getter))
        final MethodNode node = owner.addSyntheticMethod(methodName, 0x11, returnType, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, getterCode)
        node.addAnnotation(GENERATED_ANNOTATION_NODE)
    }

    private static void generateToString(final ClassNode owner) {
        if (owner.getDeclaredMethod(TO_STRING, Parameter.EMPTY_ARRAY)) return

        final Statement code = GeneralUtils.block(GeneralUtils.returnS(generateGStringExpression(owner)))
        final MethodNode node = owner.addMethod(TO_STRING, 0x11, ClassHelper.STRING_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, code)
        node.addAnnotation(GENERATED_ANNOTATION_NODE)
    }

    private static Expression generateGStringExpression(final ClassNode owner) {
        return new GStringExpression(
                'Mojo[${id} -> ${clazz}]',
                ['Mojo[', ' -> ', ']'].collect(GeneralUtils.&constX) as List<ConstantExpression>,
                [
                        GeneralUtils.propX(GeneralUtils.thisPropX(false, MOJO_CONTAINER_NAME), 'modId'),
                        GeneralUtils.propX(GeneralUtils.thisPropX(false, 'class'), 'name')
                ] as List<Expression>
        )
    }
}
