package DummyCore.Utils;

/**
 * The same as @optional, but rather than giving it mods to check against you can check against actual classes
 * @author modbder
 *
 */
public @interface ExistanceCheck {
	
	String[] classPath();

}
