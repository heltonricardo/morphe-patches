package app.morphe.patches.waze.map.streetlabels

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.waze.shared.Constants.COMPATIBILITY_WAZE
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions

private const val EXTENSION_CLASS = "Lapp/morphe/extension/waze/StreetLabelsPatch;"

@Suppress("unused")
val hideStreetLabelsPatch = bytecodePatch(
    name = "Hide street labels",
    description = "Removes the street name balloons from the Waze map.",
) {
    compatibleWith(COMPATIBILITY_WAZE)
    extendWith("extensions/waze.mpe")

    execute {
        val onCreate = mutableClassDefBy("Lcom/waze/mobile/WazeMobileApplication;")
            .methods.single { it.name == "onCreate" }
        onCreate.addInstructions(
            0,
            "invoke-static { p0 }, $EXTENSION_CLASS->init(Landroid/app/Application;)V",
        )
    }
}
