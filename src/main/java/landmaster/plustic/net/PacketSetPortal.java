package landmaster.plustic.net;

import io.netty.buffer.*;
import landmaster.plustic.traits.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import slimeknights.tconstruct.library.utils.*;

public class PacketSetPortal implements IMessage, IMessageHandler<PacketSetPortal, IMessage> {
	@Override
	public IMessage onMessage(PacketSetPortal message, MessageContext ctx) {
		IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.getEntityWorld();
		mainThread.addScheduledTask(() -> {
			EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
			if (ep.getEntityWorld().isRemote)
				return;
			NBTTagCompound nbt = TagUtil.getTagSafe(ep.getHeldItemMainhand());
			if (TinkerUtil.hasTrait(nbt, NickOfTime.nickOfTime.identifier)) {
				Vec3d eye = ep.getPositionVector().addVector(0, ep.getEyeHeight(), 0);
				Vec3d look = ep.getLookVec().scale(5);
				RayTraceResult rtr = ep.getEntityWorld().rayTraceBlocks(eye, eye.add(look));
				if (rtr == null || rtr.getBlockPos() == null) return;
				NBTTagCompound nick = new NBTTagCompound();
				nick.setInteger("x", rtr.getBlockPos().getX());
				nick.setInteger("y", rtr.getBlockPos().getY());
				nick.setInteger("z", rtr.getBlockPos().getZ());
				nick.setInteger("dim", ep.getEntityWorld().provider.getDimension());
				nbt.setTag("nickoftime", nick);
				ep.getHeldItemMainhand().setTagCompound(nbt);
				ep.addChatMessage(new TextComponentTranslation(
						"msg.plustic.nickmodifier.set", nick.getInteger("x"),
						nick.getInteger("y"), nick.getInteger("z"), nick.getInteger("dim")));
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}
}