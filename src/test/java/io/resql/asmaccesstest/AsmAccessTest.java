package io.resql.asmaccesstest;

import org.junit.*;
import org.objectweb.asm.*;
import org.slf4j.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;

import static org.objectweb.asm.Opcodes.*;

public class AsmAccessTest {
	private static Logger log = LoggerFactory.getLogger( AsmAccessTest.class );
	public AsmAccessTest() {}

	private class MyAnnotationVisitor extends AnnotationVisitor {
		MyAnnotationVisitor(int api) {
			super(api);
		}

		@Override
		public void visit(String name, Object value) {
			log.debug( "ANNOTATION name: {}, value: {}", name, value );
		}

		@Override
		public void visitEnum(String name, String desc, String value) {
			log.debug( "ANNOTATION ENUM name: {}, desc: {}, value: {}", name, desc, value );
		}

		@Override
		public AnnotationVisitor visitAnnotation(String name, String desc) {
			log.debug( "ANNOTATION ANNOTATION name: {}, desc: {} [[", name, desc );
			return new MyAnnotationVisitor( ASM5 );
		}

		@Override
		public AnnotationVisitor visitArray(String name) {
			log.debug( "ANNOTATION ARRAY name: {} [[", name );
			return new MyAnnotationVisitor( ASM5 );
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
		}
	}

	private class ClassScanner extends ClassVisitor {
		private ClassScanner() {
			super( ASM5 );
		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			log.debug( "Visited {} {} {} {} {}", access, name, desc, signature, value );
			return super.visitField(access, name, desc, signature, value);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			super.visitMethod(access, name, desc, signature, exceptions);
			log.debug( "METHOD BEGIN {} {} {} {} {}", access, name, desc, signature, exceptions );
			return new MethodVisitor( ASM5 ) {
				@Override
				public void visitInsn(int opcode) {
					log.debug( "INSN: {}", opcode );
				}

				@Override
				public void visitVarInsn(int opcode, int var) {
					log.debug( "VAR INSN {} for var {}", opcode, var );
				}

				@Override
				public void visitFieldInsn(int opcode, String owner, String name, String desc) {
					log.debug( "FIELD INSN {} for owner: {}, name: {}, desc: {} ", opcode, owner, name, desc );
				}

				@Override
				public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
					log.debug(
							"METHOD INSN {}, owner: {}, name: {}, desc: {}, itf: {}",
							opcode, owner, name, desc, itf
					);
				}

				@Override
				public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
					log.debug( "TypeAnnotation typeRef: {}, typePath: {}, desc: {}, visible: {} [[",
							typeRef, typePath, desc, visible
					);
					return new MyAnnotationVisitor( ASM5 );
				}

				@Override
				public AnnotationVisitor visitAnnotationDefault() {
					log.debug( "AnnotationDefault [[" );
					return new MyAnnotationVisitor( ASM5 );
				}

				@Override
				public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
					log.debug( "Annotation desc: {}, visible: {} [[", desc, visible );
					return new MyAnnotationVisitor( ASM5 );
				}

				@Override
				public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
					log.debug( "ParameterAnnotation parameter: {}, desc: {}, visible: {} [[", parameter, desc, visible );
					return new MyAnnotationVisitor( ASM5 );
				}

				@Override
				public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
					log.debug( "LocalVariableAnnotation typeRef: {}, typePath: {}, start: {}, end: {}, index: {}, desc: {}, visible: {} [[",
							typeRef, typePath, start, end, index, desc, visible );
					return new MyAnnotationVisitor( ASM5 );
				}

				@Override
				public void visitMaxs(int maxStack, int maxLocals) {
					log.debug( "MAXS: maxStack: {}, maxLocals: {}", maxStack, maxLocals );
				}

				@Override
				public void visitIntInsn(int opcode, int operand) {
					log.debug( "INT INSN: {}, operand: {}", opcode, operand );
				}

				@Override
				public void visitTypeInsn(int opcode, String type) {
					log.debug( "TYPE INSN: {}, type: {}", opcode, type );
				}

				@Override
				public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
					log.debug( "Local variable name: {}, desc: {}, signature: {}, start: {}, end: {}, index: {}",
							name, desc, signature, start, end, index
					);
				}

				@Override
				public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
					log.debug( "TBL SWITCH INSN: min: {}, max: {}, dflt: {}, labels: {}", min, max, dflt, labels);
				}

				@Override
				public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
					log.debug( "InvokeDynamicInsn: name: {}, desc: {}, bsm: {}, bsmArgs: {}", name, desc, bsm, bsmArgs);
				}

				@Override
				public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
					log.debug( "Frame: type: {}, nLocal: {}, local: {}, nStack: {}, stack: {}", type, nLocal, local, nStack, stack);
				}

				@Override
				public void visitParameter(String name, int access) {
					log.debug( "Parameter: name: {}, access: {}", name, access);
				}

				@Override
				public void visitMultiANewArrayInsn(String desc, int dims) {
					log.debug( "MultiANewArrayInsn: desc: {}, dims: {}", desc, dims);
				}

				@Override
				public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
					log.debug( "LookupSwitchInsn: dflt: {}, keys: {}, labels: {}", dflt, keys, labels);
				}

				@Override
				public void visitLdcInsn(Object cst) {
					log.debug( "LdcInsn: {}", cst);
				}

				@Override
				public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
					log.debug( "TryCatchBlock: start: {}, end: {}, handler: {}, type: {}", start, end, handler, type);
				}

				@Override
				public void visitIincInsn(int var, int increment) {
					log.debug( "IincInsn: var: {}, increment: {}", var, increment);
				}

				@Override
				public void visitLineNumber(int line, Label start) {
					log.debug( "LineNumber: line: {}, start: {}", line, start);
				}

				@Override
				public void visitJumpInsn(int opcode, Label label) {
					log.debug( "JumpInsn: opcode: {}, label: {}", opcode, label);
				}

				@Override
				public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
					log.debug( "TryCatchAnnotation: typeRef: {}, typePath: {}, desc: {}, visible: {}", typeRef, typePath, desc, visible);
					return super.visitTryCatchAnnotation(typeRef,typePath,desc,false);
				}

				@Override
				public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
					log.debug( "InsnAnnotation: typeRef: {}, typePath: {}, desc: {}, visible: {}", typeRef, typePath, desc, visible);
					return super.visitTryCatchAnnotation( typeRef,typePath,desc, visible);
				}

				@Override
				public void visitEnd() {
					super.visitEnd();
					log.debug( "METHOD END");
				}

				@Override
				public void visitAttribute(Attribute attr) {
					log.debug( "METHOD ATTRIBUTE: {}", attr );
					super.visitAttribute(attr);
				}

				@Override
				public void visitCode() {
					log.debug( "METHOD CODE" );
					super.visitCode();
				}

				@Override
				public void visitLabel(Label label) {
					log.debug( "Label: {}", label );
					super.visitLabel(label);
				}
			};
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			log.debug( "VISIT version: {}, access: {}, name: {}, signature: {}, superName: {}, interfaces: {}",
					version, access, name, signature, superName, interfaces );
		}

		@Override
		public void visitSource(String source, String debug) {
			log.debug( "SOURCE: source: {}, debug: {}", source, debug );
		}

		@Override
		public void visitOuterClass(String owner, String name, String desc) {
			log.debug( "OUTER CLASS: owner: {}, name: {}, desc: {}", owner, name, desc );
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			log.debug( "ANNOTATION: desc: {}, visible: {}", desc, visible );
			return new MyAnnotationVisitor( ASM5 );
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			log.debug( "TYPE ANNOTATION typeRef: {}, typePath: {}, desc: {}, visible: {}", typeRef, typePath, desc, visible );
			return new MyAnnotationVisitor( ASM5 );
		}

		@Override
		public void visitAttribute(Attribute attr) {
			log.debug( "ATTRIBUTE: {}", attr );
		}

		@Override
		public void visitInnerClass(String name, String outerName, String innerName, int access) {
			log.debug( "INNER CLASS name: {}, outerName: {}, innerName: {}, access: {}", name, outerName, innerName, access );
		}

		@Override
		public void visitEnd() {
			log.debug( "CLASS END" );
		}
	}

	private void createDefaultConstructor( MethodVisitor mv ) {
		// Вызов конструктора родительского класса (java.lang.Object)
		mv.visitVarInsn( ALOAD, 0 );    // загружаем this
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false );    // вызываем родительский конструктор
		mv.visitInsn( RETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private ClassAccessor createClass() throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES );
		cw.visit( V1_8, ACC_PUBLIC, "io/resql/asmaccesstest/TargetClass_SyntheticAccessor", null,
				Object.class.getName().replace('.', '/' ), new String[]{ ClassAccessor.class.getName().replace( '.', '/') }
		);

		// Конструктор по умолчанию
		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "<init>", "()V", null, null );
		createDefaultConstructor( mv );

		mv = cw.visitMethod( ACC_PUBLIC, "setOrmFields", "(Ljava/lang/Object;)V", null,
				new String[] { SQLException.class.getName().replace( '.', '/' )  } );
		mv.visitVarInsn( ALOAD, 1 );    // загружаем первый параметр метода
		mv.visitTypeInsn( CHECKCAST, "io/resql/asmaccesstest/TargetClass");
		mv.visitVarInsn( ASTORE, 2 );

		mv.visitVarInsn( ALOAD, 2 );    // загружаем первый параметр метода
		mv.visitInsn( ICONST_1 ); // загружаем целочисленную единицу
		mv.visitFieldInsn( PUTFIELD, "io/resql/asmaccesstest/TargetClass", "privateAccessInt", "I" ); // Загружаем единицу в поле

		mv.visitVarInsn( ALOAD, 2 );    // загружаем первый параметр метода
		mv.visitLdcInsn( "строка" );
		mv.visitFieldInsn( PUTFIELD, "io/resql/asmaccesstest/TargetClass", "defaultAccessString", "Ljava/lang/String;" );

		mv.visitVarInsn( ALOAD, 2 );    // загружаем первый параметр метода
		mv.visitLdcInsn( 2.0 );
		mv.visitFieldInsn( PUTFIELD, "io/resql/asmaccesstest/TargetClass", "publicAccessDouble", "D" ); // Загружаем единицу в поле

		mv.visitVarInsn( ALOAD, 2 );    // загружаем первый параметр метода
		mv.visitVarInsn( ALOAD, 2 );    // загружаем первый параметр метода
		mv.visitFieldInsn( PUTFIELD, "io/resql/asmaccesstest/TargetClass", "protectedMySelf", "Lio/resql/asmaccesstest/TargetClass;" ); // Загружаем единицу в поле


		mv.visitInsn( RETURN );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();

		byte[] classBytes = cw.toByteArray();
		ClassLoader classLoader = getClass().getClassLoader();
		Class classLoaderClass = classLoader.getClass();
		Method foundMethod = null;
top:
		do {
			for (Method method : classLoaderClass.getDeclaredMethods() ) {
				if ( method.getName().equals( "defineClass" ) ) {
					log.debug( "At class {} method {}", classLoaderClass, method );
					Class< ? >[] params = method.getParameterTypes();
					if ( params.length == 4
							&& params[ 0 ].equals( String.class )
							&& params[ 1 ].equals( byte[].class )
							&& params[ 2 ].equals( int.class )
							&& params[ 3 ].equals( int.class )
							) {
						log.debug( "FOUND!" );
						foundMethod = method;
						break top;
					}
				}
			}
			classLoaderClass = classLoaderClass.getSuperclass();
		} while ( classLoaderClass != null );
		// Method method = classLoader.getClass().getDeclaredMethod( "defineClass", String.class, byte[].class, int.class, int.class );
		if ( foundMethod == null ) {
			throw new RuntimeException( "Method not found!" );
		}
		foundMethod.setAccessible( true );
		Class clazz = (Class) foundMethod.invoke( classLoader, "io.resql.asmaccesstest.TargetClass_SyntheticAccessor", classBytes, 0, classBytes.length );
		return (ClassAccessor) clazz.<ClassAccessor>getDeclaredConstructor().newInstance();
	}

	@Ignore( "Perspective feature" )
	@Test
	public void testAccessStandard() throws Exception {
		TargetClass tc = new TargetClass();
		TargetClassAccessor tca = new TargetClassAccessor();
		tca.setOrmFields( tc );
		tc.checkValuesSet();
	}

	@Ignore( "Perspective feature" )
	@Test
	public void testAccess() throws Exception {
		Field field = TargetClass.class.getDeclaredField( "privateAccessInt" );
		field.setAccessible( true );
		TargetClass tc = new TargetClass();
		log.debug( "private field value: {}", field.get( tc ) );
		ClassAccessor ca = createClass();
		ca.setOrmFields( tc );
		tc.checkValuesSet();
	}

	@Ignore( "Perspective feature performance evaluation - ~4x speed" )
	@Test
	public void evaluateSpeed() throws Exception {
		final int arrSize = 1000;
		final int repeat = 1000000;

		TargetClass[] classArr = new TargetClass[ arrSize ];
		for ( int j = 0; j < arrSize; ++ j ) {
			classArr[ j ] = new TargetClass();
		}

		long start;
		long end;

		Field f1 = TargetClass.class.getDeclaredField( "privateAccessInt" );
		Field f2 = TargetClass.class.getDeclaredField( "defaultAccessString" );
		Field f3 = TargetClass.class.getDeclaredField( "publicAccessDouble" );
		Field f4 = TargetClass.class.getDeclaredField( "protectedMySelf" );
		start = System.nanoTime();

		for ( int i = 0 ; i < repeat; ++ i ) {
			for ( int j = 0; j < arrSize; ++ j ) {
				TargetClass tc = classArr[ j ];
				f1.setInt( tc, 1 );
				f2.set( tc, "строка" );
				f3.setDouble( tc, 2.0 );
				f4.set( tc, tc );
			}
		}
		end = System.nanoTime();
		log.debug( "Reflection time is {}", end - start );


		ClassAccessor accessor = createClass();

		start = System.nanoTime();
		for ( int i = 0 ; i < repeat; ++ i ) {
			for ( int j = 0; j < arrSize; ++ j ) {
				accessor.setOrmFields(classArr[j]);
			}
		}
		end = System.nanoTime();
		log.debug( "Direct time is {}", end - start );


	}

	@Ignore( "Perspective feature" )
	@Test
	public void testAccessor() throws IOException {
		new ClassReader( "io.resql.asmaccesstest.TargetClassAccessor" ).accept( new ClassScanner(), 0 );
	}

	@Ignore( "Perspective feature" )
	@Test
	public void classLoader() throws IOException {
		Enumeration<URL> iter = getClass().getClassLoader().getResources("");
		while ( iter.hasMoreElements() ) {
			log.debug( "{}", iter.nextElement() );
		}
	}

}
