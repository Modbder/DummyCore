package DummyCore.Client;

import net.minecraft.client.gui.GuiMainMenu;
import DummyCore.Utils.IMainMenu;

/**
 * This is a vanilla Main Menu as it is. As you can see there is literally nothing mine in this class - it is the regular and most basic GuiMainMenu, just with the IMainMenu interface on top of it
 * <br>All instanceof checks run just fine
 * <br>And if a modmaker is comparing a class object to checkif the gui is the vanilla's gui - well, you could check for this class aswell, I guess...
 * <br>Still calls all forge calls/rendering hooks
 * @author modbder
 *
 */
public class GuiMainMenuVanilla extends GuiMainMenu implements IMainMenu{

}
