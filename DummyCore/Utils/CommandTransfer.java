package DummyCore.Utils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandTransfer extends CommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "DummyCore.Transfer";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/DummyCore.Transfer <player> <dimensionID>";
	}

	@Override
	public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
		// TODO Auto-generated method stub
    	int var3 = parseIntWithMin(p_71515_1_, p_71515_2_[1], Integer.MIN_VALUE);
        EntityPlayerMP player = p_71515_2_.length == 0 ? getCommandSenderAsPlayer(p_71515_1_) : getPlayer(p_71515_1_, p_71515_2_[0]);
        player.travelToDimension(var3);
	}

}
