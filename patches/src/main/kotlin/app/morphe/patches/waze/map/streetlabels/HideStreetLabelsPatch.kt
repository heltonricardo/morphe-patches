package app.morphe.patches.waze.map.streetlabels

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.waze.shared.Constants.COMPATIBILITY_WAZE

@Suppress("unused")
val hideStreetLabelsPatch = bytecodePatch(
    name = "Hide street labels",
    description = "Removes the street name balloons from the Waze map.",
) {
    compatibleWith(COMPATIBILITY_WAZE)

    execute {
        mutableClassDefBy("Lcom/waze/jni/protos/map/MapContent\$NavigationContent\$ShowActiveRoute;")
            .methods.single { it.name == "setShowRoutingLabels" }
            .addInstructions(0, "const/4 p1, 0x0")
    }
}
