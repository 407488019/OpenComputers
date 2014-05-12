package li.cil.oc.server.driver.item

import java.io
import li.cil.oc.{api, OpenComputers, Settings}
import li.cil.oc.api.driver.Slot
import li.cil.oc.server.component
import net.minecraft.item.ItemStack
import net.minecraftforge.common.DimensionManager

object Loot extends Item {
  override def worksWith(stack: ItemStack) = isOneOf(stack, api.Items.get("lootDisk"))

  override def createEnvironment(stack: ItemStack, container: component.Container) =
    createEnvironment(stack, 0, container)

  override def slot(stack: ItemStack) = Slot.Disk

  override def tier(stack: ItemStack) = 0

  private def createEnvironment(stack: ItemStack, capacity: Int, container: component.Container) = {
    if (stack.hasTagCompound) {
      val lootPath = "loot/" + stack.getTagCompound.getString(Settings.namespace + "lootPath")
      val savePath = new io.File(DimensionManager.getCurrentSaveRootDirectory, Settings.savePath + lootPath)
      val fs =
        if (savePath.exists && savePath.isDirectory) {
          api.FileSystem.fromSaveDirectory(lootPath, 0, false)
        }
        else {
          api.FileSystem.fromClass(OpenComputers.getClass, Settings.resourceDomain, lootPath)
        }
      val label =
        if (dataTag(stack).hasKey(Settings.namespace + "fs.label")) {
          dataTag(stack).getString(Settings.namespace + "fs.label")
        }
        else null
      api.FileSystem.asManagedEnvironment(fs, label, container.tileEntity.orNull)
    }
    else null
  }
}