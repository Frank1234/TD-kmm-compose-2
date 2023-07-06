package domain.model

sealed class ZIndex(val value: Float) {
    object Bullet : ZIndex(1000f)
    object TowerControls : ZIndex(2000f)
}