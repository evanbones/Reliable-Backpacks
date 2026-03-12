package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.client.ClientConfigSetup;
import com.evandev.reliable_backpacks.client.ReliableBackpacksClient;
import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.common.events.EntityInteractionEvents;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.networking.BackpackPayloadHandler;
import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ReliableBackpacks {
    public static final String NETWORK_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "main"),
            () -> NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );
    private static boolean registered = false;

    public ReliableBackpacks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

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

        if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) {
            event.register(ForgeRegistries.Keys.BLOCKS, helper -> helper.register(new ResourceLocation(Constants.MOD_ID, "backpack"), BPBlocks.BACKPACK));
        } else if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            event.register(ForgeRegistries.Keys.ITEMS, helper -> helper.register(new ResourceLocation(Constants.MOD_ID, "backpack"), BPItems.BACKPACK));
        } else if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES)) {
            event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> helper.register(new ResourceLocation(Constants.MOD_ID, "backpack"), BPBlockEntities.BACKPACK));
        } else if (event.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS)) {
            event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> {
                helper.register(new ResourceLocation(Constants.MOD_ID, "block.backpack.place"), BPSounds.BACKPACK_PLACE);
                helper.register(new ResourceLocation(Constants.MOD_ID, "block.backpack.open"), BPSounds.BACKPACK_OPEN);
                helper.register(new ResourceLocation(Constants.MOD_ID, "block.backpack.close"), BPSounds.BACKPACK_CLOSE);
                helper.register(new ResourceLocation(Constants.MOD_ID, "item.backpack.equip"), BPSounds.BACKPACK_EQUIP);
            });
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

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> toolsTab = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("tools_and_utilities"));

        if (event.getTabKey() == toolsTab) {
            event.accept(BPItems.BACKPACK);
        }
    }
}