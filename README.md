# 这长矛有力气 · Awesome Spears

> 本分支 / This branch: **1.20.1Forge**

[中文](#中文) · [English](#english)

> Author / 作者：**Arqiu-owo** · Code: `LGPL-3.0-only` · Assets: `CC-BY-NC-SA-4.0` — see [LICENSE.md](LICENSE.md)

---

## 中文

突进附魔太爱吃饭了，它怎么吃得比我还多？突进长矛看起来是个很好的鞘翅起飞装置，但它居然不能在空中用？
我长矛击退哪去了？为什么怪在我攻击范围里我戳不到它？明明我瞄着天，为什么还是只能横着冲？
相信我，安装这个模组之后，以上的烦恼统统会烟消云散的。希望如此。

### 功能

**突进（Lunge 附魔）**

- 突进不消耗饥饿值，饿着肚子也能突进。
- 鞘翅飞行时、在水里也能突进。
- 朝准星方向突进，包括上下。
- 右键蓄力时按左键才会突进，这时只突进、不刺击。
- 刺击后马上右键蓄力，也会突进，并跳过蓄力的起手动作。

**冲锋**

- 冲锋命中时击退目标。
- 冲锋命中目标后不减速，也不会停止疾跑。

**命中判定**

- 目标贴着你时也能刺中（离目标不到 2 格时伤害减半）。
- 长矛的命中判定更宽，更容易刺中。
- 按目标的移动补一次判定，打移动中的目标不用留提前量。

### 配置

每个功能都能单独开关，默认全部开启；关掉后恢复原版的效果（1.21.1 / 1.20.1 是恢复 Spear Core 的效果）。
配置文件是 `config/awesome_spears.json`，也可以在游戏里打开配置界面：Fabric 版通过 Mod Menu，NeoForge / Forge 版在模组列表里点「配置」。

客户端和服务端都要安装。原版 1.21.1 / 1.20.1 没有长矛，这两个版本是 [Spear Core](https://modrinth.com/mod/spear-core) 的附属，需要先装 Spear Core。

### 支持的版本

| 分支 | Minecraft | 加载器 |
|---|---|---|
| [`26.1.2Fabric`](../../tree/26.1.2Fabric) | 26.1.2 | Fabric Loader 0.18.4+（Mod Menu 可选） |
| [`26.1.2NeoForge`](../../tree/26.1.2NeoForge) | 26.1.2 | NeoForge 26.1.2.103+ |
| [`26.3Fabric`](../../tree/26.3Fabric) | 26.3 | Fabric Loader 0.19.3+（Mod Menu 可选） |
| [`26.3NeoForge`](../../tree/26.3NeoForge) | 26.3 | NeoForge 26.3.0.51-beta+ |
| [`1.21.1NeoForge`](../../tree/1.21.1NeoForge) | 1.21.1 | NeoForge 21.1.244+，需要 [Spear Core](https://modrinth.com/mod/spear-core) 3.2.0+ |
| [`1.20.1Forge`](../../tree/1.20.1Forge) | 1.20.1 | Forge 47+，需要 [Spear Core](https://modrinth.com/mod/spear-core) 3.2.0+ |

每个版本是仓库里的一个**分支**（`26.3Fabric` 是默认分支），分支根目录就是 Gradle 工程，更新记录见 [CHANGELOG.md](CHANGELOG.md)。

### 自己编译

这个分支附带了 Gradle Wrapper（Gradle 8.8，ForgeGradle 6 需要它），需要 **JDK 17 或 21**
（Gradle 8.8 跑不了 Java 25，运行前把 `JAVA_HOME` 设成 JDK 17/21）。

编译前先下载 [Spear Core](https://modrinth.com/mod/spear-core) 1.20.1 Forge 版 3.2.1，改名为 `spearcore-1.20.1-forge-3.2.1.jar` 放进 `libs/` 文件夹（jar 不进仓库）。然后在根目录运行：

```
gradlew build
```

jar 会生成在 `build/libs/` 里。

---

## English

The Lunge enchantment is an absolute glutton — how does it eat even more than I do? A Lunge spear looks like the perfect
elytra launcher, so why can't it be used in the air? Where did the knockback go? Why can't I hit a mob that's clearly in range?
I'm aiming at the sky, so why do I still dash sideways? Trust me: install this mod and all these frustrations vanish. Hopefully.

### Features

**Lunge enchantment**

- Lunging costs no hunger, so you can lunge even when starving.
- Lunge works while flying with an elytra and in water.
- You lunge where you aim, including up and down.
- Lunge triggers when you left-click while bracing (holding right-click); it then only lunges and does not stab.
- Right-click to brace right after a stab and you also lunge, skipping the bracing wind-up.

**Charge**

- A charge hit knocks the target back.
- Hitting a target while charging doesn't slow you down or stop your sprint.

**Hit detection**

- You can stab targets right up against you (damage is halved within 2 blocks).
- A wider spear hitbox makes hits easier to land.
- An extra check based on the target's movement, so you don't need to lead moving targets.

### Configuration

Every feature can be turned on or off on its own; all are on by default, and turning one off restores the vanilla behaviour
(on 1.21.1 / 1.20.1, Spear Core's behaviour). The config file is `config/awesome_spears.json`; there is also an in-game
config screen — via Mod Menu on Fabric, or the "Config" button in the mod list on NeoForge / Forge.

Install on both client and server. Vanilla 1.21.1 / 1.20.1 has no spears, so those two versions are add-ons for
[Spear Core](https://modrinth.com/mod/spear-core) and need it installed.

### Supported versions

| Branch | Minecraft | Loader |
|---|---|---|
| [`26.1.2Fabric`](../../tree/26.1.2Fabric) | 26.1.2 | Fabric Loader 0.18.4+ (Mod Menu optional) |
| [`26.1.2NeoForge`](../../tree/26.1.2NeoForge) | 26.1.2 | NeoForge 26.1.2.103+ |
| [`26.3Fabric`](../../tree/26.3Fabric) | 26.3 | Fabric Loader 0.19.3+ (Mod Menu optional) |
| [`26.3NeoForge`](../../tree/26.3NeoForge) | 26.3 | NeoForge 26.3.0.51-beta+ |
| [`1.21.1NeoForge`](../../tree/1.21.1NeoForge) | 1.21.1 | NeoForge 21.1.244+, requires [Spear Core](https://modrinth.com/mod/spear-core) 3.2.0+ |
| [`1.20.1Forge`](../../tree/1.20.1Forge) | 1.20.1 | Forge 47+, requires [Spear Core](https://modrinth.com/mod/spear-core) 3.2.0+ |

Each version lives on its own **branch** (`26.3Fabric` is the default branch); the branch root is the Gradle project. See [CHANGELOG.md](CHANGELOG.md) for the changelog.

### Building

This branch ships a Gradle Wrapper (Gradle 8.8, required by ForgeGradle 6) and needs **JDK 17 or 21**
(Gradle 8.8 cannot run on Java 25 — point `JAVA_HOME` at JDK 17/21 first).

Before building, download [Spear Core](https://modrinth.com/mod/spear-core) 3.2.1 for 1.20.1 Forge, rename it to `spearcore-1.20.1-forge-3.2.1.jar` and put it in `libs/` (jars are not committed). Then run, in the repository root:

```
gradlew build
```

The jar is written to `build/libs/`.

---

## 贡献者 / Contributors

- **Arqiu-owo** — 设计、美术、文案、调试 / Design, art, writing and testing
- **Claude**（Anthropic）— 程序、项目管理 / Programming and project management

## License / 许可证

- Code / 代码：[GNU LGPL v3.0](COPYING.LESSER)（with [GPL v3.0](COPYING)）
- Assets (images) / 素材（图片）：[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)

Details / 详情：[LICENSE.md](LICENSE.md)
