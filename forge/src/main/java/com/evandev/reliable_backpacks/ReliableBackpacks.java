package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.client.ClientConfigSetup;
import com.evandev.reliable_backpacks.client.ReliableBackpacksClient;
import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.common.events.EntityInteractionEvents;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.networking.BackpackPayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ReliableBackpacks {
    private static boolean registered = false;

    public static final String NETWORK_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "main"),
            () -> NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );

    public ReliableBackpacks(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register();
            modEventBus.addListener(ReliableBackpacksClient::registerLayers);
            modEventBus.addListener(ReliableBackpacksClient::registerRenderers);
            modEventBus.addListener(ReliableBackpacksClient::addPlayerLayers);
        }

        MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(this::onRightClickItem);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
    }

    private void onRegister(RegisterEvent event) {
        if (!registered) {
            Backpacks.init();
            registered = true;
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CHANNEL.registerMessage(0, BackpackOpenPayload.class,
                (payload, buf) -> {
                    buf.writeBoolean(payload.isOpen());
                    buf.writeInt(payload.id());
                },
                buf -> new BackpackOpenPayload(buf.readBoolean(), buf.readInt()),
                (payload, ctxSupplier) -> {
                    NetworkEvent.Context ctx = ctxSupplier.get();
                    ctx.enqueueWork(() -> {
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                            BackpackPayloadHandler.handleClientData(payload, Minecraft.getInstance().player);
                        });
                    });
                    ctx.setPacketHandled(true);
                }
        );
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = BackpackPickupEvents.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = BackpackPickupEvents.onRightClickItem(event.getEntity(), event.getHand());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = EntityInteractionEvents.onEntityInteract(event.getEntity(), event.getTarget());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}