package github.chvs.develop.led_04

import android.hardware.camera2.CameraManager
import java.util.*
import kotlin.random.Random.Default.nextFloat

class UpdateTimeTask(val mCameraManager: CameraManager, val mCameraId: String) : TimerTask() {//Способ передачи данных 1

    private val size_data: Int = 22//Количество передаваемых битов
    private var Data = Array(size_data, {i -> false})//Инициализация массива передаваемых данных (старт, 8 бит нулей, бит чётности, стоп) х2
    private var CurrentIndex: Int = 0//Текущий индекс передаваемых данных
    var BufData = Array(size_data, {i -> false})//Инициализация массива буфера передаваемых данных

    private var current_state: Boolean = false
    private var start1: Int = 0

    var current_button: Int = 0
    var previous_button: Int = -1
    var period: Int = -1
    var counter: Int = 500
    private var rand: Float = 0f


    init {
        //mCameraManager.setTorchMode(mCameraId, true)
    }

    fun Int2Array(int21_data: Int){
        for (i in 0..21){
            BufData[i] = ((int21_data shr i) and 1) == 1
        }
    }

    /*override fun run() {
        if (Data[CurrentIndex]) mCameraManager.setTorchMode(mCameraId, false)
        else mCameraManager.setTorchMode(mCameraId, true)

        CurrentIndex++
        if (CurrentIndex >= size_data) {
            CurrentIndex = 0
            if (Data != BufData) Data = BufData//Если данные в буфере изменились, то обновить данные
        }
    }*/

    override fun run() {

        /*if(current_button == previous_button) {
            start1 = 13 + current_button * 6
            previous_button = -1
        }
        else {
            start1 = 10 + current_button * 6
            previous_button = current_button
        }*/


        if(counter < 500) {
            if (current_state && (CurrentIndex >= period)) {
                mCameraManager.setTorchMode(mCameraId, false)
                current_state = false
                CurrentIndex = 0
                return
            }
            if ((current_state == false) && (CurrentIndex >= period)) {
                mCameraManager.setTorchMode(mCameraId, true)
                current_state = true
                CurrentIndex = 0
                return
            }
            CurrentIndex++
            counter++
        }
        if(counter == 500) {
            mCameraManager.setTorchMode(mCameraId, false)
            current_state = false
            CurrentIndex = 0
            counter++
            return
        }

    }

    /*override fun run() {
        if ((Data[CurrentIndex]) && (start1 < 5)) //Если передаётся единица и передалась только часть, то передать следующую часть
        {
            start1++
            return
        }

        start1 = 0

        if (current_state){
            mCameraManager.setTorchMode(mCameraId, false)
            current_state = false
        }
        else{
            mCameraManager.setTorchMode(mCameraId, true)
            current_state = true
        }

        CurrentIndex++
        if (CurrentIndex >= size_data) {
            CurrentIndex = 0
            if (Data != BufData) Data = BufData//Если данные в буфере изменились, то обновить данные
        }
    }*/
}
