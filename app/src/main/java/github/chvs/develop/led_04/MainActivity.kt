package github.chvs.develop.led_04

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mTimerPeriodDefault: Long = 1//Период срабатывания таймера в мс
    //private val mTimerPeriodDefault: Long = 10//Период срабатывания таймера в мс
    private val mTimerDelayDefault: Long = 0//Задержка запуска таймера в мс
    private var mText: String = ""//Выводимый текст
    private val mMaxTextlength: Int = 10//Максимальная длина выводимого текста
    private var mCurrentKeyboard: Boolean = false//Текущая клавиатура (false - цифры, true - стрелки)
    private var mCurrentKeyIndex: Int = 0//Текущий номер нажатой клавиши
    private var mCurrentData: Int = 0//Текущие передаваемые данные

    //val periods: IntArray = intArrayOf(7, 9, 11, 12, 14, 15, 17, 19, 21, 23, 26, 28, 31, 35, 39, 43, 48, 53, 59, 65, 73, 81, 89, 99)
    //val periods: IntArray = intArrayOf(7, 8, 11, 14, 18, 21, 25, 28, 32, 35, 39, 43, 5, 5)
    val periods: IntArray = intArrayOf(7, 8, 11, 14, 18, 21, 25, 29, 35, 41, 51, 62, 5, 5)
    //                                 0  1   2   3   4   5   6   7   8   9  ent del
    //val periods: IntArray = intArrayOf(3, 4, 6, 7, 8, 11, 15, 19, 25, 32, 39, 47, 5, 5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val mCameraManager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val mCameraId: String = mCameraManager.getCameraIdList()[0]
        val mUpdateTimeTask = UpdateTimeTask(mCameraManager, mCameraId)
        val mTimer = Timer()
        mTimer.scheduleAtFixedRate(mUpdateTimeTask, mTimerDelayDefault, mTimerPeriodDefault)

        updateGraph(0)
        graph.getGridLabelRenderer().setTextSize(20f)
        graph.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.horizontal_title))
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30f)


        b0.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 0) }
        b1.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 1) }
        b2.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 2) }
        b3.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 3) }
        b4.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 4) }
        b5.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 5) }
        b6.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 6) }
        b7.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 7) }
        b8.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 8) }
        b9.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 9) }
        bent.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 10) }
        bdel.setOnClickListener { OnPressedButton(mUpdateTimeTask, textView, 11) }
        barrows.setOnClickListener {

            if (mCurrentKeyboard){//Переключиться на цифры
                b0.visibility = View.VISIBLE
                b1.visibility = View.VISIBLE
                b3.visibility = View.VISIBLE
                b5.visibility = View.VISIBLE
                b7.visibility = View.VISIBLE
                b9.visibility = View.VISIBLE

                b2.setText("2")
                b4.setText("4")
                b6.setText("6")
                b8.setText("8")

                mCurrentKeyboard = false
                OnPressedButton(mUpdateTimeTask, textView, 13)
            }
            else {//Переключиться на стрелки
                b0.visibility = View.INVISIBLE
                b1.visibility = View.INVISIBLE
                b3.visibility = View.INVISIBLE
                b5.visibility = View.INVISIBLE
                b7.visibility = View.INVISIBLE
                b9.visibility = View.INVISIBLE

                b2.setText("")
                b4.setText("")
                b6.setText("")
                b8.setText("")

                mCurrentKeyboard = true
                OnPressedButton(mUpdateTimeTask, textView, 12)
            }
        }
    }

    fun OnPressedButton(mUpdateTimeTask: UpdateTimeTask, mTextView: TextView, but_number: Int){

        if (mCurrentKeyboard){//Если стрелочная клавиатура
            if (but_number == 2) mText += "↓"
            if (but_number == 4) mText += "←"
            if (but_number == 6) mText += "→"
            if (but_number == 8) mText += "↑"
        }
        else{//Если цифровая клавиатура
            if (but_number < 10) mText += but_number.toString()//Добавить цифру
        }

        if (but_number == 10) mText += "¶"//Знак ввода
        if (but_number == 11) mText += "«"//Знак удаления
        if (but_number == 12) mText += "↕"//Знак перехода на стрелки
        if (but_number == 13) mText += "❶"//Знак перехода на цифры

        mCurrentData = CreateData(mCurrentKeyIndex, but_number)//Сформировать данные для передачи
        updateGraph(mCurrentData)//Обновить график с данными
        mUpdateTimeTask.Int2Array(mCurrentData)//Записать новые данные в буфер таймера



        //По частоте
        mUpdateTimeTask.counter = 0
        mUpdateTimeTask.period = periods[but_number]
        /*if(mUpdateTimeTask.previous_button == but_number){
            mUpdateTimeTask.period = periods[(2 * but_number) + 1]//14 + but_number * 6
            mUpdateTimeTask.previous_button = -1
        }
        else{
            mUpdateTimeTask.period =  periods[2 * but_number]//11 + but_number * 6
            mUpdateTimeTask.previous_button = but_number
        }*/






        if (mText.length > mMaxTextlength) mText = mText.substring(mText.length - mMaxTextlength, mText.length)//Удалить старые символы
        mTextView.setText(mText)//Вывести текст

        mCurrentKeyIndex++//Увеличить индекс нажатой клавиши
        if (mCurrentKeyIndex > 14) mCurrentKeyIndex = 0//Индекс нажатой клавишы не более 14
    }

    fun CreateData(mCurIndex: Int, but_num: Int): Int{//Формирует данные для передачи

        val byte_1: Int = 15 + (mCurIndex shl 4)//Формирование первого байта
        val byte_2: Int = but_num + (but_num shl 4)//Формирование второго байта

        var even_1: Int = byte_1 and 1//Записывается первый бит первого байта
        for (i in 1..7){//Расчитывается чётность первого байта
            even_1 = ((byte_1 shr i) and 1) xor even_1
        }

        var even_2: Int = byte_2 and 1//Записывается первый бит второго байта
        for (i in 1..7){//Расчитывается чётность второго байта
            even_2 = ((byte_2 shr i) and 1) xor even_2
        }

        //   старт  данные          чётность        старт    данные           чётность
        return 1 + (byte_1 shl 1) + (even_1 shl 9) + 2048 + (byte_2 shl 12) + (even_2 shl 20)
    }

    fun updateGraph(data: Int) {
        graph.removeAllSeries()
        val series = LineGraphSeries(
            arrayOf(
                DataPoint(0.0, getValueFromData(data, 0)),
                DataPoint(1.0, getValueFromData(data, 0)),
                DataPoint(1.0, getValueFromData(data, 1)),
                DataPoint(2.0, getValueFromData(data, 1)),
                DataPoint(2.0, getValueFromData(data, 2)),
                DataPoint(3.0, getValueFromData(data, 2)),
                DataPoint(3.0, getValueFromData(data, 3)),
                DataPoint(4.0, getValueFromData(data, 3)),
                DataPoint(4.0, getValueFromData(data, 4)),
                DataPoint(5.0, getValueFromData(data, 4)),
                DataPoint(5.0, getValueFromData(data, 5)),
                DataPoint(6.0, getValueFromData(data, 5)),
                DataPoint(6.0, getValueFromData(data, 6)),
                DataPoint(7.0, getValueFromData(data, 6)),
                DataPoint(7.0, getValueFromData(data, 7)),
                DataPoint(8.0, getValueFromData(data, 7)),
                DataPoint(8.0, getValueFromData(data, 8)),
                DataPoint(9.0, getValueFromData(data, 8)),
                DataPoint(9.0, getValueFromData(data, 9)),
                DataPoint(10.0, getValueFromData(data, 9)),
                DataPoint(10.0, getValueFromData(data, 10)),
                DataPoint(11.0, getValueFromData(data, 10)),
                DataPoint(11.0, getValueFromData(data, 11)),
                DataPoint(12.0, getValueFromData(data, 11)),
                DataPoint(12.0, getValueFromData(data, 12)),
                DataPoint(13.0, getValueFromData(data, 12)),
                DataPoint(13.0, getValueFromData(data, 13)),
                DataPoint(14.0, getValueFromData(data, 13)),
                DataPoint(14.0, getValueFromData(data, 14)),
                DataPoint(15.0, getValueFromData(data, 14)),
                DataPoint(15.0, getValueFromData(data, 15)),
                DataPoint(16.0, getValueFromData(data, 15)),
                DataPoint(16.0, getValueFromData(data, 16)),
                DataPoint(17.0, getValueFromData(data, 16)),
                DataPoint(17.0, getValueFromData(data, 17)),
                DataPoint(18.0, getValueFromData(data, 17)),
                DataPoint(18.0, getValueFromData(data, 18)),
                DataPoint(19.0, getValueFromData(data, 18)),
                DataPoint(19.0, getValueFromData(data, 19)),
                DataPoint(20.0, getValueFromData(data, 19)),
                DataPoint(20.0, getValueFromData(data, 20)),
                DataPoint(21.0, getValueFromData(data, 20)),
                DataPoint(21.0, getValueFromData(data, 21)),
                DataPoint(22.0, getValueFromData(data, 21))
            )
        )

        graph.addSeries(series);

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(22.0)
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(1.0)
    }

    fun getValueFromData(data: Int, index: Int): Double {
        val temp_int: Int = (((data shr index) and 1))
        if (temp_int == 1) return 0.0
        else return 1.0
    }

}
