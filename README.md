# capacitor-plugin-agora

Boilerplate for Capacitor Plugin

## Install

```bash
npm install capacitor-plugin-agora
npx cap sync
```

## API

<docgen-index>

* [`joinChannel(...)`](#joinchannel)
* [`leaveChannel(...)`](#leavechannel)
* [`updateParticipantLists(...)`](#updateparticipantlists)
* [`setSpotlight(...)`](#setspotlight)
* [`showRecordingStatus(...)`](#showrecordingstatus)
* [`setCountdown(...)`](#setcountdown)
* [`showAlert(...)`](#showalert)
* [`enterPictureInPictureMode()`](#enterpictureinpicturemode)
* [`addListener('onMicrophoneChanged' | 'onCameraChanged' | 'onPlaybackDeviceChanged', ...)`](#addlisteneronmicrophonechanged--oncamerachanged--onplaybackdevicechanged)
* [`addListener('onParticipantAction', ...)`](#addlisteneronparticipantaction)
* [`addListener('onRemoteStreamChanged', ...)`](#addlisteneronremotestreamchanged)
* [`addListener('exception', ...)`](#addlistenerexception)
* [`addListener('network-quality', ...)`](#addlistenernetwork-quality)
* [`addListener('onSelfAction', ...)`](#addlisteneronselfaction)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### joinChannel(...)

```typescript
joinChannel(options: { room: string; uid: UID; user: IParticipant; token: string; appId: string; }) => Promise<UID>
```

Join Agora Channel

| Param         | Type                                                                                                                                      |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ room: string; uid: <a href="#uid">UID</a>; user: <a href="#iparticipant">IParticipant</a>; token: string; appId: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#uid">UID</a>&gt;</code>

--------------------


### leaveChannel(...)

```typescript
leaveChannel(options: { room?: string; }) => Promise<void>
```

Leave Agora Channel

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ room?: string; }</code> |

--------------------


### updateParticipantLists(...)

```typescript
updateParticipantLists(options: { participants: IParticipant[]; }) => Promise<IParticipant[]>
```

Update participant lists, including those who are not joined agora channel. Returned participants mapped with their hasJoined status.

| Param         | Type                                           |
| ------------- | ---------------------------------------------- |
| **`options`** | <code>{ participants: IParticipant[]; }</code> |

**Returns:** <code>Promise&lt;IParticipant[]&gt;</code>

--------------------


### setSpotlight(...)

```typescript
setSpotlight(options: { participantId: string; }) => Promise<UID>
```

Set a participant to spotlight. Spotlighted participant frame should be highlighted, and place reordered to 1st position in the participant list.

| Param         | Type                                    |
| ------------- | --------------------------------------- |
| **`options`** | <code>{ participantId: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#uid">UID</a>&gt;</code>

--------------------


### showRecordingStatus(...)

```typescript
showRecordingStatus(options: { isShown: boolean; }) => Promise<void>
```

Show red icon for recording status. This recording is not meant to be performed on frontend.

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ isShown: boolean; }</code> |

--------------------


### setCountdown(...)

```typescript
setCountdown(options: { seconds: number; }) => Promise<void>
```

Show a countdown. A non-positive value will disable it.

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ seconds: number; }</code> |

--------------------


### showAlert(...)

```typescript
showAlert(options: { alertText: string; }) => Promise<void>
```

Show a countdown. A non-positive value will disable it.

| Param         | Type                                |
| ------------- | ----------------------------------- |
| **`options`** | <code>{ alertText: string; }</code> |

--------------------


### enterPictureInPictureMode()

```typescript
enterPictureInPictureMode() => Promise<void>
```

Enter picture-in-picture mode

--------------------


### addListener('onMicrophoneChanged' | 'onCameraChanged' | 'onPlaybackDeviceChanged', ...)

```typescript
addListener(eventName: 'onMicrophoneChanged' | 'onCameraChanged' | 'onPlaybackDeviceChanged', listenerFunc: (deviceInfo: DeviceInfo) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Local media action events.

| Param              | Type                                                                                 |
| ------------------ | ------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'onMicrophoneChanged' \| 'onCameraChanged' \| 'onPlaybackDeviceChanged'</code> |
| **`listenerFunc`** | <code>(deviceInfo: <a href="#deviceinfo">DeviceInfo</a>) =&gt; void</code>           |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onParticipantAction', ...)

```typescript
addListener(eventName: 'onParticipantAction', listenerFunc: (participantId: string, event: 'nudge' | 'call' | 'mute' | 'unmute' | 'enableCamera' | 'disableCamera', data?: any) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Participant events

| Param              | Type                                                                                                                                             |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'onParticipantAction'</code>                                                                                                               |
| **`listenerFunc`** | <code>(participantId: string, event: 'nudge' \| 'call' \| 'mute' \| 'unmute' \| 'enableCamera' \| 'disableCamera', data?: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onRemoteStreamChanged', ...)

```typescript
addListener(eventName: 'onRemoteStreamChanged', listenerFunc: (participantId: string, event: 'join' | 'leave', data?: any) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Remote stream events

| Param              | Type                                                                                  |
| ------------------ | ------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onRemoteStreamChanged'</code>                                                  |
| **`listenerFunc`** | <code>(participantId: string, event: 'join' \| 'leave', data?: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('exception', ...)

```typescript
addListener(eventName: 'exception', listenerFunc: (event: { code: number; msg: string; uid: UID; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Exceptions

| Param              | Type                                                                                         |
| ------------------ | -------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'exception'</code>                                                                     |
| **`listenerFunc`** | <code>(event: { code: number; msg: string; uid: <a href="#uid">UID</a>; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('network-quality', ...)

```typescript
addListener(eventName: 'network-quality', listenerFunc: (stats: NetworkQuality) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Network Quality

| Param              | Type                                                                          |
| ------------------ | ----------------------------------------------------------------------------- |
| **`eventName`**    | <code>'network-quality'</code>                                                |
| **`listenerFunc`** | <code>(stats: <a href="#networkquality">NetworkQuality</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onSelfAction', ...)

```typescript
addListener(eventName: 'onSelfAction', listenerFunc: (event: 'chat' | 'leaved' | 'back' | 'exitPipMode' | 'enterPipMode', room?: string | undefined) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

User action

| Param              | Type                                                                                                            |
| ------------------ | --------------------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onSelfAction'</code>                                                                                     |
| **`listenerFunc`** | <code>(event: 'chat' \| 'leaved' \| 'back' \| 'exitPipMode' \| 'enterPipMode', room?: string) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Removes all listeners

--------------------


### Interfaces


#### IParticipant

| Prop            | Type                                              |
| --------------- | ------------------------------------------------- |
| **`uid`**       | <code><a href="#uid">UID</a></code>               |
| **`name`**      | <code>string</code>                               |
| **`role`**      | <code><a href="#clientrole">ClientRole</a></code> |
| **`avatar`**    | <code><a href="#iavatar">IAvatar</a></code>       |
| **`subtitle`**  | <code>string</code>                               |
| **`hasJoined`** | <code>boolean</code>                              |


#### IAvatar

| Prop      | Type                |
| --------- | ------------------- |
| **`url`** | <code>string</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### DeviceInfo

Information of the media input device.

- You can get the audio sampling device information through [onMicrophoneChanged]{@link onMicrophoneChanged}.
- You can get the video capture device information through [onCameraChanged]{@link onCameraChanged}.
- You can get the audio playback device information through [onPlaybackDeviceChanged]{@link onPlaybackDeviceChanged}.

| Prop           | Type                                                | Description                                                                                                                                        |
| -------------- | --------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`updateAt`** | <code>number</code>                                 | The latest time when the state of the media input device was updated. A Unix timestamp in milliseconds.                                            |
| **`initAt`**   | <code>number</code>                                 | The time when the SDK first detects the media input device. A Unix timestamp in milliseconds.                                                      |
| **`state`**    | <code><a href="#devicestate">DeviceState</a></code> | The state of the capture device.                                                                                                                   |
| **`device`**   | <code>MediaDeviceInfo</code>                        | Device information of the media input device. See [MediaDeviceInfo](https://developer.mozilla.org/en-US/docs/Web/API/MediaDeviceInfo) for details. |


#### NetworkQuality

The last-mile network quality.

Last mile refers to the connection between the local device and Agora edge server.

- After the local user joins the channel, the SDK triggers the [AgoraRTCClient.on("network-quality")]{@link IAgoraRTCClient.event_network_quality} callback once every two seconds and provides the uplink and downlink last-mile network conditions of the user through this interface.
- You can call [AgoraRTCClient.getRemoteNetworkQuality]{@link IAgoraRTCClient.getRemoteNetworkQuality} to get the network quality of all remote users to whom the local user subscribes.

&gt; The returned network quality is a relative value and is for reference only.

| Prop                         | Type                                         | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| ---------------------------- | -------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`uplinkNetworkQuality`**   | <code>0 \| 1 \| 2 \| 6 \| 5 \| 4 \| 3</code> | The uplink network quality. It is calculated based on the uplink transmission bitrate, uplink packet loss rate, RTT (round-trip time) and jitter. - 0: The quality is unknown. - 1: The quality is excellent. - 2: The quality is good, but the bitrate is less than optimal. - 3: Users experience slightly impaired communication. - 4: Users can communicate with each other, but not very smoothly. - 5: The quality is so poor that users can barely communicate. - 6: The network is disconnected and users cannot communicate.   |
| **`downlinkNetworkQuality`** | <code>0 \| 1 \| 2 \| 6 \| 5 \| 4 \| 3</code> | The downlink network quality. It is calculated based on the uplink transmission bitrate, uplink packet loss rate, RTT (round-trip time) and jitter. - 0: The quality is unknown. - 1: The quality is excellent. - 2: The quality is good, but the bitrate is less than optimal. - 3: Users experience slightly impaired communication. - 4: Users can communicate with each other, but not very smoothly. - 5: The quality is so poor that users can barely communicate. - 6: The network is disconnected and users cannot communicate. |


### Type Aliases


#### UID

The user ID to identify a user in the channel.

Each user in the same channel should have a unique user ID with the same data type (number or string).

<code>number | string</code>


#### ClientRole

The user role in a live broadcast channel.
- `"host"`: Host. A host can both publish tracks and subscribe to tracks.
- `"audience"`: Audience. An audience can only subscribe to tracks.

<code>"audience" | "host"</code>


#### DeviceState

The state of the media input device.
- `"ACTIVE"`: The device is plugged in.
- `"INACTIVE"`: The device is unplugged.

<code>"ACTIVE" | "INACTIVE"</code>

</docgen-api>
