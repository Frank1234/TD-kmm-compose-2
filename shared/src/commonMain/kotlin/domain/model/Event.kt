package domain.model

sealed class Event {
    sealed class Tower : Event() {
        data class Selected(val towerId: TowerId) : Event()
        data class ConstructButtonPressed(val towerId: TowerId, val towerType: TowerType) : Event()
    }

    sealed class Menu : Event() {
        object RestartGame : Event()
        object QuitGame : Event()
        object CloseMenu : Event()
        object OpenMenu : Event()
    }

    object ClearSelection : Event()
}