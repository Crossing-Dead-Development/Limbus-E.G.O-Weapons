# Limbus E.G.O Weapons — Fabric Mod

將邊獄公司（Limbus Company）的 E.G.O 武器帶進 Minecraft 的 Fabric Mod，由 Paper 插件移植而來。

- **Minecraft 版本**：1.21.4
- **Loader**：Fabric Loader 0.16.9
- **依賴**：Fabric API 0.119.4+1.21.4

---

## 武器一覽

### 莊嚴哀悼（黑）`solemn_lament_black`
> *稀有度：史詩*

右鍵消耗一顆**蝴蝶石英**發射彈幕，命中後造成 **8 點傷害** 並附加**凋零 II（4 秒）**。
冷卻時間：**1.2 秒**。彈幕存活上限：**5 秒（100 tick）**。

---

### 莊嚴哀悼（白）`solemn_lament_white`
> *稀有度：史詩*

與黑色版本相同機制，但命中後造成 **4 點傷害** 並附加**失明（3 秒）**。

---

### 蝴蝶石英 `butterfly_quartz`
> *稀有度：不常見，最大疊放：64*

莊嚴哀悼的專用彈藥，射擊時消耗 1 顆。

---

### 聖宣盾牌 `solemn_shield`
> *稀有度：稀有*

持有時（主手或副手）每 5 tick 對**半徑 5 格內的所有生物**施加**緩慢 II（2 秒）**，並持續產生白色粒子光環。

---

### 擬態 `mimicry`
> *稀有度：史詩 | +12 攻擊傷害 / -3.2 攻擊速度*

每次攻擊有 **10% 機率**觸發暴擊，額外造成 **40～90 點傷害**（隨機）並產生爆炸粒子。
每次攻擊額外**吸取對目標造成傷害的 25%** 恢復自身血量。

---

### DaCapo `dacapo`
> *稀有度：史詩 | +7 攻擊傷害 / -2.4 攻擊速度*

取消一般攻擊，改為連擊模式（每次攻擊同時波及**半徑 3.5 格**內的其他生物，受 70% 傷害）：

| 模式 | 機率 | 連擊數 | 單擊傷害 | 間隔 |
|------|------|--------|----------|------|
| 普通 | 60%  | 5 擊   | 4        | 2 tick |
| 特殊 | 40%  | 3 擊   | 17       | 4 tick |

---

### 環指筆刷 `ring_brush`
> *稀有度：史詩 | +8 攻擊傷害 / -2.4 攻擊速度*

**右鍵**對目標施加 **3.5 點傷害** 並隨機附加一種負面效果（失明、緩慢、毒、虛弱、凋零，持續 4 秒）。
在 **1.5 秒內對同一目標再次右鍵**，觸發雙重效果（施加兩次）；第一次右鍵會使玩家向目標方向衝刺。

---

## 指令

| 指令 | 說明 |
|------|------|
| `/getego black` | 取得莊嚴哀悼（黑） |
| `/getego white` | 取得莊嚴哀悼（白） |
| `/getego butterflies` | 取得蝴蝶石英 ×16 |
| `/getego shield` | 取得聖宣盾牌 |
| `/getego mimicry` | 取得擬態 |
| `/getego dacapo` | 取得 DaCapo |
| `/getego brush` | 取得環指筆刷 |

> 需要權限等級 **2（OP）**。

---

## 安裝方式

1. 安裝 [Fabric Loader](https://fabricmc.net/use/) 與 [Fabric API](https://modrinth.com/mod/fabric-api)
2. 將 `limbus-ego-weapons-fabric-1.0.0.jar` 放入伺服器或客戶端的 `mods/` 資料夾
3. 啟動遊戲

---

## 開發環境

```
Java 21
Gradle 9.5.0
Fabric Loom 1.9.2
```

複製專案後直接執行：

```bash
./gradlew build
```

編譯結果在 `build/libs/limbus-ego-weapons-fabric-1.0.0.jar`。
