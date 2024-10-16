package org.gongclouds.canvasdemo

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.Thread.sleep

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            Surface() {
                Game(screenWidth, screenHeight)
            }
        }
    }
}

class Player() : Sprite() {
    var dieDelay = 0
    var jump = false
    var top = 0
    var bottom = 0
}

class Enemy() : Sprite() {
    var addedScore = false
}

val player = Player()
val enemyList = arrayListOf<Enemy>()


@Composable
fun Game(screenWidth: Int, screenHeight: Int) {

    val playerImage0 = ImageBitmap.imageResource(id = R.drawable.player0)
    val playerImage1 = ImageBitmap.imageResource(id = R.drawable.player1)
    val playerImage2 = ImageBitmap.imageResource(id = R.drawable.player2)
    val playerImage3 = ImageBitmap.imageResource(id = R.drawable.player3)
    val playerImage4 = ImageBitmap.imageResource(id = R.drawable.player4)
    val enemyImage = ImageBitmap.imageResource(id = R.drawable.shit)

    var step by remember { mutableIntStateOf(0) }

    var gameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }


    fun makeAnEnemyIntoEnemyList() {
        val enemy = Enemy()
        with(enemy) {
            set {
                x = screenWidth + 50
                y = screenHeight - 50
                width = 40
                height = 40
                imageBitmap = enemyImage
            }
            loop {
                x--

                if (x < -100) {
                    die = true
                }

                if (x < -10) {
                    if (!addedScore) {
                        score += 1
                        if (score > highScore) {
                            highScore = score
                        }
                        addedScore = true
                    }
                }

                if (isCollision(this, player)) {
                    this.die = true
                    player.dieDelay = 100
                }
            }
        }
        enemyList.add(enemy)
    }

    LaunchedEffect(key1 = Unit) {
        with(player) {
            set {
                x = 30
                y = screenHeight - 60
                width = 50
                height = 50
                imageBitmap = playerImage0
                bottom = y
                top = bottom - height * 2
                dieDelay = 0
            }
            loop {
                if (!die) {
                    time++

                    if (dieDelay > 0) {
                        dieDelay--
                        if (dieDelay == 0) die = true
                    }
                    if (time % 6 == 0)

                        imageBitmap = if (dieDelay > 0) {
                            if (imageBitmap == playerImage0) playerImage3
                            else playerImage0
                        } else {
                            if (imageBitmap == playerImage1) playerImage2
                            else playerImage1
                        }


                    if (jump) {
                        y -= 2
                        if (y < top) {
                            y = top
                            jump = false
                        }
                    } else {
                        if (y < bottom) {
                            y += 2
                        }
                        if (y >= bottom) {
                            y = bottom
                        }
                    }
                }
                if (die) {
                    imageBitmap = playerImage4
                    gameOver = true
                }
            }
        }
        makeAnEnemyIntoEnemyList()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize(),
        onDraw = {
            step++

            if (step % 200 == 0 && !gameOver) makeAnEnemyIntoEnemyList()

            with(player) {
                run()

                drawImage(
                    imageBitmap,
                    dstSize = IntSize(width, height), // Resizes the image
                    dstOffset = IntOffset(x, y) // Positions the image
                )
            }

            enemyList.forEach {
                with(it) {
                    if (!gameOver) run()
                    drawImage(
                        imageBitmap,
                        dstSize = IntSize(width, height), // Resizes the image
                        dstOffset = IntOffset(x, y) // Positions the image
                    )
                }
            }
        }
    )


    if (gameOver) Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("GAME OVER", fontSize = 60.sp, color = Color.Red)
        Text("SCORE:$score", fontSize = 20.sp)
        Text("HIGH SCORE:$highScore", fontSize = 20.sp)
        Button(
            onClick = {
                player.reset()
                score = 0
                enemyList.clear()
                gameOver = false
            },
        ) {
            Text("Restart")
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "score:$score", fontSize = 30.sp, modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(10.dp, 10.dp)
            )
            Button(
                onClick = {
                    with(player) {
                        if (!jump && y == bottom) {
                            jump = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset((-10).dp, (-20).dp)
                    .alpha(0.2f)
            ) {
                Text("Jump")
            }
        }
    }

}