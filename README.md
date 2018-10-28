# UniversalMarket
A MySQL based Item Market system powered by Sponge


## Commands

All Universal Market commands can be use as /um or /universalmarket

### Open Market
`/um open, /um o, /um`

### Add to Market
`/um add, /um a`
`/um add (price)`
`/um add (price) (<optional>amount)`

### Reload Config
`/um reload, /um r`

## The Market
![Market](https://gyazo.com/aa747f5486fbe224f74984d94bbd91f6.png)
## Market Item
![MarketItem](https://gyazo.com/34bf241b733cbed513214f9d89bf177d.png)
## Market Config
![MarketConfig](https://gyazo.com/8620a0d03a31c549d692fa37a4540de6.png)

## Permissions

`com.xwaffle.universalmarket.open` - Permission to open the Universal Market.

`com.xwaffle.universalmarket.add` - Permission to add items to the Universal Market.

`com.xwaffle.universalmarket.remove` - Permission to remove a players item from being listed in the Universal Market.
![MarketRemove](https://gyazo.com/bb9fbd4406a8c85dd7f74e0adbeedb33.png)

#### If the config option `use-permission-to-sell` is equal to true

**Note When enabled, be sure to set the `total-items-player-can-sell` config option to be higher than the highest permission node you give out.*

`com.xwaffle.universalmarket.addmax.##` - Sets the amount of items a user may  sell in the Universal Market.

EX: `com.xwaffle.universalmarket.addmax.5`, this will allow the user to sell 5 items at a time in the Universal Market.

## Stats
https://bstats.org/plugin/sponge/UniversalMarket
