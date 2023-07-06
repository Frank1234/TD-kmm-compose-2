package domain.game

import domain.model.Event
import domain.model.Game
import domain.model.ToastMessage
import domain.model.ToastMessageSpecs
import domain.model.Tower
import domain.model.TowerType
import domain.model.getConstructionPrice
import domain.model.getLevelOneConstructionPrice

object EventHandler {

    fun clearSelection(game: Game) =
        game.copy(towers = game.towers.map { tower -> tower.copy(isSelected = false) })

    object TowerHandler { // TODO or TowerUseCases and own file?

        fun selectTower(game: Game, event: Event.Tower.Selected): Game {
            return game.copy(towers = game.towers.map { tower -> tower.copy(isSelected = tower.id == event.towerId) })
        }

        // TODO function naming
        fun constructOrUpdateTower(game: Game, event: Event.Tower.ConstructButtonPressed): Game {
            val selectedTower = game.towers.firstOrNull { it.id == event.towerId }
            return when (selectedTower?.type) {
                TowerType.Archer -> selectedTower.upgrade(game)
                TowerType.Empty -> selectedTower.construct(game, event.towerType)
                null -> game
            }
        }

        private fun Tower.construct(game: Game, towerType: TowerType): Game {
            val tower = this
            val price = (towerType.getLevelOneConstructionPrice() ?: 0)
            return if (game.coins >= price) {
                val updatedCoins = game.coins - price
                val updatedTowers = game.towers.toMutableList().apply {
                    remove(tower)
                    add(tower.copy(type = towerType, isSelected = false))
                }
                game.copy(coins = updatedCoins, towers = updatedTowers)
            } else {
                game.copy(
                    toastMessage = ToastMessage(ToastMessageSpecs.NotEnoughFunds)
                )
            }
        }

        private fun Tower.upgrade(game: Game): Game {
            val tower = this
            val updatePrice = tower.getConstructionPrice(type)
            return if (game.coins >= updatePrice) {
                val updatedCoins = game.coins - updatePrice
                val updatedTowers = game.towers.toMutableList().apply {
                    remove(tower)
                    add(tower.copy(level = tower.level.getNextLevel(), isSelected = false))
                }
                return game.copy(coins = updatedCoins, towers = updatedTowers)
            } else {
                game.copy(
                    toastMessage = ToastMessage(ToastMessageSpecs.NotEnoughFunds)
                )
            }
        }
    }
}