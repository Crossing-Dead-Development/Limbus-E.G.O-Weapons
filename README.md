# Limbus E.G.O Weapons — Paper Plugin

將邊獄公司（Limbus Company）的 E.G.O 武器帶進 Minecraft 的 Paper 插件。

- **版本**：2.3.3
- **Minecraft 版本**：1.21.4
- **平台**：Paper（需支援 `setItemModel` API）
- **軟相依**：[ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)（可選，用於攔截莊嚴哀悼周圍他人聽到的原版弓箭聲）
- **資源包**：插件會在玩家加入時自動推送，拒絕資源包將被踢出伺服器

---

## 武器一覽

### 莊嚴哀悼（黑）
> 材質：**弩（CROSSBOW）** | CustomModelData: 1002 | 隱藏附魔「快速上弦 V」

弩兩段式：右鍵上弦（vanilla 弩的舉手姿勢，所有玩家可見）→ 再右鍵發射。  
快速上弦 V（突破 vanilla III 上限）讓上弦近乎瞬發。附魔 tooltip 與閃光皆隱藏。

- 攔截 vanilla 箭矢，改發**自製蝴蝶投射物**（ItemDisplay，朝向跟著飛行方向）
- 命中造成 **8 點傷害** 並附加**凋零 II（4 秒）**
- 上弦時 vanilla 會吃一支彈藥進弩；發射時清空 chargedProjectiles，可立即重新上弦
- 冷卻：**400ms**（快速上弦 V 計算下的最低）
- 自訂裝填音、攔截 vanilla 弩上弦音

> ⚠ 若玩家身上同時有蝴蝶箭與普通箭，vanilla 弩可能優先選普通箭上弦。建議把蝴蝶箭放副手或 hotbar 第一格。

---

### 莊嚴哀悼（白）
> 材質：**弩（CROSSBOW）** | CustomModelData: 1003 | 隱藏附魔「快速上弦 V」

與黑色版本相同機制，命中造成 **4 點傷害** 並附加**失明（3 秒）**。

---

### 生蝶、亡蝶
> 材質：**箭（ARROW）** | CustomModelData: 1004

莊嚴哀悼的專用彈藥。底材是箭，所以 vanilla 弩會將其視為合法彈藥（不需要幽靈假箭）。  
**普通弓弩無法使用**，只有莊嚴哀悼能擊發。

---

### 聖宣
> 材質：盾牌 | CustomModelData: 1005

持有時（主手或副手）每 5 tick 對**半徑 5 格內的所有生物**施加**緩慢 II（2 秒）**，並產生白色粒子光環。

---

### 擬態
> 材質：鑽石劍 | CustomModelData: 1006 | +12 攻擊傷害 / -3.2 攻擊速度

每次攻擊有 **10% 機率**觸發暴擊，額外造成 **40～90 點傷害**（隨機）並產生爆炸粒子。  
每次攻擊額外**吸取最終傷害的 25%** 恢復自身血量（暴擊觸發後才計算，數值正確）。

---

### DaCapo
> 材質：鐵劍 | CustomModelData: 1007 | +7 攻擊傷害 / -2.4 攻擊速度

取消一般攻擊，改為連擊模式（同時波及**半徑 3.5 格**內的其他生物，受 70% 傷害）：

| 模式 | 機率 | 連擊數 | 單擊傷害 | 間隔 |
|------|------|--------|----------|------|
| 普通 | 60%  | 5 擊   | 4        | 2 tick |
| 特殊 | 40%  | 3 擊   | 17       | 4 tick |

---

### 環指筆刷
> 材質：下界合金劍 | CustomModelData: 1001 | +8 攻擊傷害 / -2.4 攻擊速度

**右鍵**對目標施加 **3.5 點傷害** 並隨機附加一種負面效果（失明、緩慢、毒、虛弱、凋零）。  
在 **1.5 秒內對同一目標再次右鍵**，觸發雙重效果；第一次右鍵會使玩家向目標方向衝刺。

---

### 天退星刀
> 材質：**下界合金劍（NETHERITE_SWORD）** | CustomModelData: 1008 | +8 攻擊傷害 / -2.4 攻擊速度

居合衝刺。近戰刀 + 消耗實體子彈（火藥）發動定身蓄力 → 向前衝刺，衝刺路徑上的敵人受傷（無投射物，子彈僅為助推火藥）。

| 操作 | 蓄力 | 速度 | 距離 | 傷害 | 額外效果 |
|------|------|------|------|------|----------|
| 右鍵（虎標彈） | 1 秒 | 中 | 中 | 8 | 燃燒 3 秒 |
| 潛行右鍵（猛虎標彈） | 3 秒 | 高 | 遠 | 18 | 燃燒 5 秒 + 凋零 II（3 秒） |

- 蓄力期間**定身**（重緩速、不可移動）；**受擊中斷**，不消耗子彈
- 自訂揮刀音（手持時所有揮動都換音，三種變體隨機）
- 蓄力音、衝刺釋放音皆為自訂；蓄力中斷時 `stopSound` 停掉所有蓄力音

#### 虎標彈
> 材質：**火藥（GUNPOWDER）** | 天退星刀右鍵蓄力消耗

#### 猛虎標彈
> 材質：**火藥（GUNPOWDER）** | 天退星刀潛行右鍵蓄力消耗

#### 插翅虎（組合包）
> 材質：**試煉鑰匙（TRIAL_KEY）** | 圖示：lei.png

**右鍵開啟** → 給予 1 把天退星刀 + 10 個猛虎標彈 + 20 個虎標彈，自身消耗 1 個並消失。

- 背包需 **4 格空位**才能開啟，否則 ActionBar 提示
- 刀會放到 storage（slot 9-35），不會進主手 → 開包後不會立刻誤觸發蓄力
- 開啟無聲，只有粒子提示

---

## 指令

| 指令 | 說明 |
|------|------|
| `/getego brush` | 取得環指筆刷 |
| `/getego mimicry` | 取得擬態 |
| `/getego dacapo` | 取得 DaCapo |
| `/getego tiantui` | 取得天退星刀 |
| `/getego tiger_mark` | 取得虎標彈 |
| `/getego savage_tiger_mark` | 取得猛虎標彈 |
| `/getego chatuhu` | 取得插翅虎組合包 |
| `/getego black` | 取得莊嚴哀悼（黑）|
| `/getego white` | 取得莊嚴哀悼（白）|
| `/getego butterflies` | 取得生蝶、亡蝶 |
| `/getego shield` | 取得聖宣 |
| `/getego admin` | 開啟管理員 GUI（36 格，含天退星系列 + 插翅虎） |
| `/getego give <玩家> <武器ID> [數量]` | 給予指定玩家武器（可由主控台執行；`[數量]` 預設 1） |

> 需要權限節點 `limbus.admin` 或 OP（主控台不受限）。
>
> 武器 ID：`brush`、`mimicry`、`dacapo`、`tiantui`、`tiger_mark`、`savage_tiger_mark`、`chatuhu`、`black`、`white`、`butterflies`、`shield`

---

## 聲音方案（莊嚴哀悼）

vanilla 對「射手本人」的攻擊音是**客戶端預測播放**，伺服器封包攔截不到（封包根本不送給射手）。本插件採分層方案：

| 對象 | 處理 | 需求 |
|------|------|------|
| **別人**聽你的莊嚴哀悼弓聲 | ProtocolLib 攔截座標/實體音封包 | 需裝 ProtocolLib（可選） |
| **射手本人**聽自己的預測音 | Fabric 客戶端 mod 攔截 `SoundManager.play` | 玩家自裝 [fabric-1.0.1](https://github.com/EvansGoethe/Limbus-E.G.O-Weapons/releases/tag/fabric-1.0.1) |

---

## 資源包

插件使用獨立資源包提供武器外觀，玩家加入時會自動推送。  
資源包來源：[Limbus-E.G.O-weapon-plugin-ResourcePack](https://github.com/EvansGoethe/Limbus-E.G.O-weapon-plugin-ResourcePack)  
目前版本：**v.2.10**

各武器的 `customModelData` 及基底材質已記錄於上方武器一覽，可供外部插件（如 BattlePass）直接引用。

---

## 安裝方式

1. 將編譯好的 `.jar` 放入伺服器的 `plugins/` 資料夾
2.（可選）安裝 ProtocolLib，啟用莊嚴哀悼周圍的弓聲攔截
3. 啟動伺服器，資源包會自動推送給玩家
4. 玩家可選裝 Fabric 客戶端 mod [fabric-1.0.1](https://github.com/EvansGoethe/Limbus-E.G.O-Weapons/releases/tag/fabric-1.0.1)（Fabric Loader + Fabric API），消除自己聽到的原版弓箭預測音

---

## Fabric 移植版

本插件已移植至 Fabric 1.21.4，請見 `master` branch。
