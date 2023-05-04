package vn.wellcare.plugins.capacitor.agora.util

import com.getcapacitor.JSObject
import java.util.*

enum class ClientRole(val value: String) {
    AUDIENCE("audience"),
    HOST("host")
}

data class IParticipant(
    val _id: String?,
    var name: String,
    var avatar: IAvatar,
    val role: ClientRole,
    val subtitle: String,
    var hasJoined: Boolean,
    var uid: String
) {
    constructor(jsObject: JSObject) : this(
        UUID.randomUUID().toString(),
        jsObject.getString("name", "")!!,
        if (jsObject.has("avatar")) {
            val avatarJS = jsObject.getJSObject("avatar")
            val avatarUrl = avatarJS?.getString("url") ?: ""
            IAvatar(avatarUrl)
        } else {
            IAvatar("")
        },
        ClientRole.valueOf(jsObject.getString("role", "host")!!.toUpperCase()),
        jsObject.getString("subtitle", "")!!,
        jsObject.getBoolean("hasJoined", false)!!,
        jsObject.getString("uid", UUID.randomUUID().toString())!!
    )
}

//{
//    constructor(_id: String?, name: String, avatar: IAvatar, role: ClientRole, subtitle: String, hasJoined: Boolean, uid: String) : this(_id, name, avatar, role, subtitle, hasJoined, uid)
//
//    constructor(jsObject: JSObject) : this(
//    UUID.randomUUID().toString(),
//    jsObject.getString("name", ""),
//    if (jsObject.has("avatar")) {
//        val avatarJS = jsObject.getJSObject("avatar")
//        val avatarUrl = avatarJS?.getString("url") ?: ""
//        IAvatar(avatarUrl)
//    } else {
//        IAvatar("")
//    },
//    ClientRole.valueOf(jsObject.getString("role", "host")!!.toUpperCase()),
//    jsObject.getString("subtitle", ""),
//    jsObject.getBoolean("hasJoined", false),
//    jsObject.getString("uid", UUID.randomUUID().toString())
//    )
//}
data class IAvatar(val url: String)

enum class IParticipantAction(val value: Int) {
    CALL(0),
    NUDGE(1),
    MUTE(2),
    UNMUTE(3),
    ENABLE_CAMERA(4),
    DISABLE_CAMERA(5);

    val event: String
        get() = when (this) {
            CALL -> "call"
            NUDGE -> "nudge"
            MUTE -> "mute"
            UNMUTE -> "unmute"
            ENABLE_CAMERA -> "enableCamera"
            DISABLE_CAMERA -> "disableCamera"
        }
}

enum class AgoraControlButton(val value: Int) {
    FLIP(0),
    CAMERA(1),
    CALL(2),
    MIC(3),
    CHAT(4)
}