package org.gongclouds.canvasdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            Surface(modifier = Modifier.padding(0.dp)) {
                Greeting(
                    modifier = Modifier.padding(0.dp)
                )
            }
        }
    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var score by remember { mutableIntStateOf(0) }
    var jump by remember { mutableStateOf(false) }

    var gameStatus by remember { mutableStateOf(GameStatus.Ready) }

    val playerImages = arrayListOf<ImageBitmap>()
    playerImages.add(ImageBitmap.imageResource(id = R.drawable.dino0))
    playerImages.add(ImageBitmap.imageResource(id = R.drawable.dino1))
    playerImages.add(ImageBitmap.imageResource(id = R.drawable.dino0))
    playerImages.add(ImageBitmap.imageResource(id = R.drawable.dino2))

    val playerGoToDieImages = arrayListOf<ImageBitmap>()
    playerGoToDieImages.add(ImageBitmap.imageResource(id = R.drawable.dino0))
    playerGoToDieImages.add(ImageBitmap.imageResource(id = R.drawable.dino3))

    val playerDeadImages = arrayListOf<ImageBitmap>()
    playerDeadImages.add(ImageBitmap.imageResource(id = R.drawable.dino4))

    val enemyImages = arrayListOf<ImageBitmap>()
    enemyImages.add(ImageBitmap.imageResource(id = R.drawable.shit))

    val player by remember { mutableStateOf(Sprite.create("player")) }
    val enemy by remember { mutableStateOf(Sprite.create("enemy")) }


    var frame by remember { mutableIntStateOf(0) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        drawIntoCanvas {
            val screenWidth = it.nativeCanvas.width
            val screenHeight = it.nativeCanvas.height



            frame++

            if (gameStatus == GameStatus.Ready) {

                with(player) {
                    var dieTime = 0
                    setup {
                        setImages(playerImages)
                        dieTime = 0
                        setSize(50, 50)
                        setLocation(10, 160)
                    }
                    loop {
                        if (getStatus() == SpriteStatus.Living) {
                            val (_, y) = getLocation()
                            if (getTime() % 2 == 0) {
                                if (jump) {
                                    setLocationY(y - 5)
                                    if (getLocation().y < 80) {
                                        jump = false
                                    }
                                } else {
                                    setLocationY(getLocation().y + 10)
                                    if (getLocation().y >= 160) {
                                        setLocationY(160)
                                    }
                                }
                            }
                        }

                        if (getStatus() == SpriteStatus.GoingToDie) {
                            dieTime += 1
                            if (dieTime >= 50) {
                                setImages(playerDeadImages)
                                setStatus(SpriteStatus.Dead)
                                gameStatus = GameStatus.GameOver
                            }
                        }
                    }
                }

                with(enemy) {
                    setup {
                        setImages(enemyImages)
                        setSize(40, 40)
                        setLocation(screenWidth, 170)
                    }
                    loop {
                        if (getStatus() == SpriteStatus.Living) {
                            val x = getLocation().x
                            setLocationX(getLocation().x - 2)
                            if (x < -1 * getSize().w) {
                                setLocationX(screenWidth + getSize().w)
                                score += 1
                            }
                            if (player.getStatus() == SpriteStatus.Living) {
                                if (intersects(player)) {
                                    player.setImages(playerGoToDieImages)
                                    player.setStatus(SpriteStatus.GoingToDie)
                                    player.setLocationY(160)
                                    setStatus(SpriteStatus.Dead)
                                }
                            }
                        }
                    }
                }
            }


            if (gameStatus == GameStatus.Gaming) {

                with(enemy) {
                    run()
                }
                with(player) {
                    run()
                }
            }

            if (gameStatus != GameStatus.LoadingRes && gameStatus != GameStatus.Ready) {

                with(enemy) {
                    val (x, y) = getLocation()
                    val (w, h) = getSize()
                    drawImage(
                        getImage(),
                        dstSize = IntSize(width = w, height = h), // Resizes the image
                        dstOffset = IntOffset(x = x, y = y) // Positions the image
                    )
                }

                with(player) {
                    val (x, y) = getLocation()
                    val (w, h) = getSize()
                    drawImage(
                        getImage(),
                        dstSize = IntSize(width = w, height = h), // Resizes the image
                        dstOffset = IntOffset(x = x, y = y) // Positions the image
                    )
                }
            }
        }
    }

    if (gameStatus == GameStatus.Ready) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {

            Button(onClick = {
                gameStatus = GameStatus.Gaming
            }) {
                Text(
                    text = "Start"
                )
            }
        }
    }

    if (gameStatus == GameStatus.Gaming) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "$score",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(10.dp, 30.dp)
            )

            Button(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-20).dp, y = 20.dp),
                onClick = {
                    gameStatus = GameStatus.Paused
                }) {
                Text(
                    text = "Pause"
                )
            }

            Button(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-20).dp, y = (-20).dp),
                onClick = {
                    if (!jump) {
                        jump = true
                    }
                }) {
                Text(
                    text = "Jump"
                )
            }
        }
    }

    if (gameStatus == GameStatus.Paused) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Button(onClick = {
                    gameStatus = GameStatus.Gaming
                }) {
                    Text(
                        text = "Play"
                    )
                }
            }
        }
    }

    if (gameStatus == GameStatus.GameOver) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    "Game Over",
                    fontSize = 30.sp,
                    color = Color.Red,
                    modifier = Modifier
                )
                Button(onClick = {
                    player.reset()
                    enemy.reset()
                    gameStatus = GameStatus.Gaming
                }) {
                    Text(
                        text = "Play Again"
                    )
                }
            }
        }
    }
}