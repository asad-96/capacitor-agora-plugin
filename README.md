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
leaveChannel({ room }?: string | undefined) => any
```

Leave Agora Channel

| Param     | Type                |
| --------- | ------------------- |
| **`__0`** | <code>string</code> |

**Returns:** <code>any</code>

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

</docgen-api>
