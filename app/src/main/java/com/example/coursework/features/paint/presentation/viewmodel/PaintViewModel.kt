package com.example.coursework.features.paint.presentation.viewmodel

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.R
import com.example.coursework.core.models.ImageSize
import com.example.coursework.features.paint.data.PaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import kotlin.math.sqrt


enum class Tool(
    val displayName: String,
    @DrawableRes val icon: Int,
    val action: PaintAction
) {
    Line(
        displayName = "Линия",
        icon = R.drawable.baseline_show_chart_24,
        action = PaintAction.SelectLineTool
    ),
    Circle(
        displayName = "Окружность",
        icon = R.drawable.baseline_circle_24,
        action = PaintAction.SelectCircleTool
    )
}

sealed interface PaintUiEvent {

}

sealed interface PaintNavigationEvent {
    data object NavigateBack : PaintNavigationEvent
}

sealed interface PaintAction {
    data object NavigateBack : PaintAction

    data object ToggleBrush : PaintAction
    data object SelectEraser : PaintAction
    data class SelectColor(val color: Color) : PaintAction
    data class ChangeBrushSize(val size: Int) : PaintAction
    data object SelectLineTool : PaintAction
    data object SelectCircleTool : PaintAction
    data class ChangeCircleRadius(val radius: Float) : PaintAction

    data object SelectFill : PaintAction
    data object SaveImage : PaintAction
    data object UpdateImage : PaintAction
    data object SaveImageAs : PaintAction
    data class SaveImageWithName(val name: String) : PaintAction
    data object ClearScreen : PaintAction
    data class DrawPixel(val x: Int, val y: Int) : PaintAction
    data object UndoLastAction : PaintAction
    data object RedoLastAction : PaintAction


    data class ChangeSaveMenuVisibility(val isVisible: Boolean) : PaintAction
    data class ChangeGridVisibility(val isVisible: Boolean) : PaintAction
    data class ChangeColorPickerVisibility(val isVisible: Boolean) : PaintAction
    data class ChangeClearDialogVisibility(val isVisible: Boolean) : PaintAction
    data class ChangeSaveImageWithNameDialogVisibility(val isVisible: Boolean) : PaintAction
}


data class PaintState(
    val isLoading: Boolean = true,
    val error: Boolean = false,

    val isBrushEnabled: Boolean = false,
    val isEraserEnabled: Boolean = false,
    val currentColor: Color = Color.Black,
    val isGridVisible: Boolean = true,
    val brushSize: Int = 1,
    val isFillEnabled: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,

    val isLineToolSelected: Boolean = false,
    val isCircleToolSelected: Boolean = false,
    val pendingLineStart: Offset? = null,

    val currentTool: Tool = Tool.Line,
    val toolList: List<Tool> = Tool.entries.toList(),
    val currentCircleRadius: Float = 5f,

    val imageSize: ImageSize = ImageSize.XS,
    val pixels: Array<IntArray> = emptyArray(),
    val imageName: String? = null,
    val imageId: Int? = null,

    val isSaveMenuVisible: Boolean = false,
    val isColorPickerDialogVisible: Boolean = false,
    val isClearDialogVisible: Boolean = false,
    val isSaveImageWithNameDialogVisible: Boolean = false,
)

@HiltViewModel
class PaintViewModel @Inject constructor(
    private val paintRepository: PaintRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaintState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PaintUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _navigationEvents = MutableSharedFlow<PaintNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private var lastDrawTime = System.currentTimeMillis()
    private val drawDelay = 500L

    private val undoStack = mutableListOf<Array<IntArray>>()
    private val redoStack = mutableListOf<Array<IntArray>>()

    private val maxStackSize = 50

    fun getPaintScreen(
        imageSize: ImageSize?,
        imageId: Int?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                withContext(Dispatchers.IO) {
                    when {
                        imageId != null -> {
                            val image = paintRepository.getImageById(imageId)
                            _state.update {
                                it.copy(
                                    imageId = image.id,
                                    imageSize = image.imageSize,
                                    imageName = image.name,
                                    pixels = image.pixels.toTypedArray()
                                )
                            }
                        }

                        imageSize != null -> {
                            _state.update {
                                it.copy(imageSize = imageSize)
                            }

                            makeClearScreen()
                        }

                        else -> {
                            val lastImage = paintRepository.getLastImage().firstOrNull()
                            if (lastImage != null) {
                                _state.update { state ->
                                    state.copy(
                                        pixels = lastImage.pixels.toTypedArray(),
                                        imageSize = lastImage.imageSize,
                                        imageName = lastImage.name,
                                        imageId = lastImage.id
                                    )
                                }
                            } else {
                                _state.update {
                                    it.copy(imageSize = ImageSize.XXS)
                                }
                                makeClearScreen()
                            }
                        }
                    }
                }
            } catch (e: Exception) {

            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun saveStateForUndo() {
        val currentPixelsCopy = state.value.pixels.map { it.copyOf() }.toTypedArray()

        undoStack.add(currentPixelsCopy)

        if (undoStack.size > maxStackSize) {
            undoStack.removeAt(0)
        }

        if (undoStack.size >= 2) {
            val last = undoStack[undoStack.size - 1]
            val secondLast = undoStack[undoStack.size - 2]
            if (arePixelsEqual(last, secondLast)) {
                undoStack.removeAt(undoStack.size - 1)
            }
        }

        redoStack.clear()

        _state.update {
            it.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    private fun undo() {
        if (undoStack.isNotEmpty()) {

            val previousPixels = undoStack.removeAt(undoStack.size - 1)


            val currentPixelsCopy = state.value.pixels.map { it.copyOf() }.toTypedArray()
            redoStack.add(currentPixelsCopy)


            _state.update { it.copy(pixels = previousPixels, canUndo = undoStack.isNotEmpty(), canRedo = redoStack.isNotEmpty()) }
        }
    }

    private fun redo() {
        if (redoStack.isNotEmpty()) {

            val nextPixels = redoStack.removeAt(redoStack.size - 1)


            val currentPixelsCopy = state.value.pixels.map { it.copyOf() }.toTypedArray()
            undoStack.add(currentPixelsCopy)


            _state.update { it.copy(pixels = nextPixels, canUndo = undoStack.isNotEmpty(), canRedo = redoStack.isNotEmpty()) }
        }
    }

    private fun makeClearScreen() {
        _state.update {
            it.copy(
                pixels = Array(state.value.imageSize.size) {
                    IntArray(state.value.imageSize.size) {
                        Color.White.toArgb()
                    }
                },
                pendingLineStart = null
            )
        }
    }

    private fun arePixelsEqual(pixels1: Array<IntArray>, pixels2: Array<IntArray>): Boolean {
        if (pixels1.size != pixels2.size) return false
        for (i in pixels1.indices) {
            if (!pixels1[i].contentEquals(pixels2[i])) return false
        }
        return true
    }

    private fun drawPixel(x: Int, y: Int) {
        val size = state.value.imageSize.size
        val brushSize = state.value.brushSize
        val radius = brushSize

        for (i in (x - radius + 1)..(x + radius - 1)) {
            for (j in (y - radius + 1)..(y + radius - 1)) {
                if (i in 0 until size && j in 0 until size) {
                    state.value.pixels[i][j] = state.value.currentColor.toArgb()
                }
            }
        }
        scheduleImageSave()
    }

    private fun erasePixel(x: Int, y: Int) {
        if (x in 0 until state.value.imageSize.size && y in 0 until state.value.imageSize.size) {
            state.value.pixels[x][y] = Color.White.toArgb()
        }
        scheduleImageSave()
    }

    private fun scheduleImageSave() {
        lastDrawTime = System.currentTimeMillis()

        viewModelScope.launch {
            delay(drawDelay)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastDrawTime >= drawDelay) {
                saveLastImage()
            }
        }
    }

    private suspend fun saveLastImage() {
        paintRepository.saveLastImage(
            state.value.imageSize,
            state.value.pixels.toList(),
            state.value.imageName,
            state.value.imageId
        )
    }

    private suspend fun saveImageWithName(imageId: Int?) {
        state.value.imageName?.let { name ->
            val id = paintRepository.saveImage(
                imageSize = state.value.imageSize,
                pixels = state.value.pixels.toList(),
                name = name,
                id = imageId
            )
            _state.update {
                it.copy(
                    imageId = id
                )
            }
            saveLastImage()
        } ?: _state.update {
            it.copy(isSaveImageWithNameDialogVisible = true)
        }
    }



    private fun fillArea(startX: Int, startY: Int) {
        val size = state.value.imageSize.size
        val targetColor = state.value.pixels[startX][startY]
        val replacementColor = state.value.currentColor.toArgb()

        if (targetColor == replacementColor) return

        val queue: Queue<Pair<Int, Int>> = LinkedList()
        queue.add(Pair(startX, startY))

        while (queue.isNotEmpty()) {
            val (x, y) = queue.remove()

            if (x < 0 || x >= size || y < 0 || y >= size) continue
            if (state.value.pixels[x][y] != targetColor) continue

            state.value.pixels[x][y] = replacementColor

            queue.add(Pair(x + 1, y))
            queue.add(Pair(x - 1, y))
            queue.add(Pair(x, y + 1))
            queue.add(Pair(x, y - 1))
        }

        scheduleImageSave()
    }


    fun onAction(action: PaintAction) {
        when (action) {
            is PaintAction.UndoLastAction -> undo()
            is PaintAction.RedoLastAction -> redo()
            is PaintAction.DrawPixel,
            is PaintAction.ClearScreen -> {
                saveStateForUndo()
                handleAction(action)
            }
            is PaintAction.ChangeCircleRadius,
            is PaintAction.SelectCircleTool,
            is PaintAction.SelectLineTool,
            is PaintAction.ToggleBrush,
            is PaintAction.SelectEraser,
            is PaintAction.SelectFill,
            is PaintAction.ChangeBrushSize,
            is PaintAction.SelectColor,
            is PaintAction.UpdateImage,
            is PaintAction.SaveImageWithName,
            is PaintAction.ChangeGridVisibility,
            is PaintAction.NavigateBack,
            is PaintAction.SaveImage,
            is PaintAction.SaveImageAs,
            is PaintAction.ChangeSaveMenuVisibility,
            is PaintAction.ChangeColorPickerVisibility,
            is PaintAction.ChangeClearDialogVisibility,
            is PaintAction.ChangeSaveImageWithNameDialogVisibility -> {
                handleAction(action)
            }
        }
    }

    private fun handleAction(action: PaintAction) {
        when (action) {
            is PaintAction.NavigateBack -> {
                viewModelScope.launch {
                    saveLastImage()
                    _navigationEvents.emit(PaintNavigationEvent.NavigateBack)
                }
            }

            is PaintAction.ToggleBrush -> {
                _state.update {
                    it.copy(
                        isBrushEnabled = !it.isBrushEnabled,
                        isEraserEnabled = false,
                        isFillEnabled = false,
                        isLineToolSelected = false,
                        isCircleToolSelected = false,
                        pendingLineStart = null
                    )
                }
            }

            is PaintAction.SelectEraser -> {
                _state.update {
                    it.copy(
                        isEraserEnabled = !it.isEraserEnabled,
                        isBrushEnabled = false,
                        isFillEnabled = false,
                        isLineToolSelected = false,
                        isCircleToolSelected = false,
                        pendingLineStart = null
                    )
                }
            }

            is PaintAction.SelectFill -> {
                _state.update {
                    it.copy(
                        isFillEnabled = true,
                        isBrushEnabled = false,
                        isEraserEnabled = false,
                        isLineToolSelected = false,
                        isCircleToolSelected = false,
                        pendingLineStart = null
                    )
                }
            }

            is PaintAction.SelectColor -> {
                _state.update { it.copy(currentColor = action.color) }
            }

            is PaintAction.ChangeColorPickerVisibility -> {
                _state.update { it.copy(isColorPickerDialogVisible = action.isVisible) }
            }

            is PaintAction.ChangeGridVisibility -> {
                _state.update {
                    it.copy(
                        isGridVisible = action.isVisible
                    )
                }
            }

            is PaintAction.ChangeClearDialogVisibility -> {
                _state.update {
                    it.copy(
                        isClearDialogVisible = action.isVisible
                    )
                }
            }

            is PaintAction.SaveImage -> {
                viewModelScope.launch {
                    if (state.value.imageName == null) {
                        _state.update {
                            it.copy(isSaveImageWithNameDialogVisible = true)
                        }
                    } else {
                        _state.update {
                            it.copy(isSaveMenuVisible = true)
                        }
                    }
                }
            }

            is PaintAction.SaveImageWithName -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(imageName = action.name)
                    }
                    saveImageWithName(null)
                }
            }

            is PaintAction.SaveImageAs -> {
                _state.update {
                    it.copy(isSaveImageWithNameDialogVisible = true)
                }
            }

            is PaintAction.UpdateImage -> {
                viewModelScope.launch {
                    saveImageWithName(state.value.imageId)
                }
            }

            is PaintAction.DrawPixel -> handleDrawPixel(action.x, action.y)

            PaintAction.ClearScreen -> makeClearScreen()

            is PaintAction.ChangeSaveImageWithNameDialogVisibility -> {
                _state.update {
                    it.copy(isSaveImageWithNameDialogVisible = action.isVisible)
                }
            }

            is PaintAction.ChangeSaveMenuVisibility -> {
                _state.update {
                    it.copy(isSaveMenuVisible = action.isVisible)
                }
            }

            is PaintAction.ChangeBrushSize -> {
                _state.update { it.copy(brushSize = action.size) }
            }

            is PaintAction.SelectLineTool -> selectLineTool()

            is PaintAction.SelectCircleTool -> {
                _state.update {
                    it.copy(
                        currentTool = Tool.Circle,
                        isCircleToolSelected = !state.value.isCircleToolSelected,
                        isLineToolSelected = false,
                        isBrushEnabled = false,
                        isEraserEnabled = false,
                        isFillEnabled = false,
                        pendingLineStart = null
                    )
                }
            }

            is PaintAction.ChangeCircleRadius -> {
                _state.update {
                    it.copy(
                        currentCircleRadius = action.radius
                    )
                }
            }

            else -> {}
        }
    }

    private fun selectLineTool() {
        _state.update {
            it.copy(
                currentTool = Tool.Line,
                isLineToolSelected = !state.value.isLineToolSelected,
                isBrushEnabled = false,
                isEraserEnabled = false,
                isFillEnabled = false,
                pendingLineStart = null,
                isCircleToolSelected = false
            )
        }
    }


    private fun handleDrawPixel(x: Int, y: Int) {
        when {
            _state.value.isLineToolSelected -> {
                val start = _state.value.pendingLineStart
                if (start == null) {
                    _state.update {
                        it.copy(pendingLineStart = Offset(x.toFloat(), y.toFloat()))
                    }
                } else {
                    saveStateForUndo()
                    drawLineOnPixels(start, Offset(x.toFloat(), y.toFloat()), _state.value.brushSize)
                    _state.update {
                        it.copy(pendingLineStart = Offset(x.toFloat(), y.toFloat()))
                    }
                }
            }
            _state.value.isCircleToolSelected -> {
                saveStateForUndo()
                drawCircle(x, y, _state.value.currentCircleRadius, _state.value.currentColor.toArgb())
            }
            _state.value.isBrushEnabled -> drawPixel(x, y)
            _state.value.isEraserEnabled -> erasePixel(x, y)
            _state.value.isFillEnabled -> fillArea(x, y)
            else -> {}
        }
    }

    private fun drawCircle(x: Int, y: Int, radius: Float, color: Int) {
        val size = state.value.imageSize.size
        val brushThickness = state.value.brushSize.toFloat()

        for (i in 0 until size) {
            for (j in 0 until size) {
                val dx = (i - x).toDouble()
                val dy = (j - y).toDouble()
                val distance = sqrt(dx * dx + dy * dy)

                if (distance >= radius - brushThickness && distance <= radius + brushThickness) {
                    state.value.pixels[i][j] = color
                }
            }
        }
        scheduleImageSave()
    }

    // Алгоритм Брезенхема
    private fun drawLineOnPixels(start: Offset, end: Offset, brushSize: Int) {
        val pixels = state.value.pixels
        val size = state.value.imageSize.size
        val color = state.value.currentColor.toArgb()

        val (x0, y0) = start
        val (x1, y1) = end

        var dx = Math.abs(x1.toInt() - x0.toInt())
        var dy = Math.abs(y1.toInt() - y0.toInt())

        var sx = if (x0 < x1) 1 else -1
        var sy = if (y0 < y1) 1 else -1

        var err = if (dx > dy) dx / 2 else -dy / 2
        var x = x0.toInt()
        var y = y0.toInt()

        while (true) {
            drawCircleAtPixel(x, y, brushSize, color, pixels, size)

            if (x == x1.toInt() && y == y1.toInt()) break
            val e2 = err

            if (e2 > -dx) {
                err -= dy
                x += sx
            }

            if (e2 < dy) {
                err += dx
                y += sy
            }
        }

        val newPixels = pixels.map { it.copyOf() }.toTypedArray()

        _state.update { it.copy(pixels = newPixels, canUndo = undoStack.isNotEmpty(), canRedo = redoStack.isNotEmpty()) }
    }

    private fun drawCircleAtPixel(x: Int, y: Int, brushSize: Int, color: Int, pixels: Array<IntArray>, size: Int) {
        val radius = brushSize / 2
        for (i in (x - radius)..(x + radius)) {
            for (j in (y - radius)..(y + radius)) {
                if (i in 0 until size && j in 0 until size) {
                    val dx = (i - x).toDouble()
                    val dy = (j - y).toDouble()
                    if (dx * dx + dy * dy <= radius * radius) {
                        pixels[i][j] = color
                    }
                }
            }
        }
    }
}