package DummyCore.Utils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Internal. A command to switch dimensions
 * @author modbder
 *
 */
public class CommandTransfer extends CommandBase {

	@Override
	public String getName() {
		return "DummyCore.Transfer";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/DummyCore.Transfer <player> <dimensionID>";
	}

	@Override
	public void execute(ICommandSender p_71515_1_, String[] p_71515_2_) {
		try{
	    	int var3 = parseInt(p_71515_2_[1],Integer.MIN_VALUE , Integer.MAX_VALUE);
	        EntityPlayerMP player = p_71515_2_.length == 0 ? getCommandSenderAsPlayer(p_71515_1_) : getPlayer(p_71515_1_, p_71515_2_[0]);
	        player.travelToDimension(var3);
		}catch(Exception e){}
	}

}
