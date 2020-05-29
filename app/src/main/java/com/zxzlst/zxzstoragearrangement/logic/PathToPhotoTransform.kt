package com.zxzlst.zxzstoragearrangement.logic

import android.util.Log
import java.io.*
import java.net.URLEncoder

//val file = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
fun filePathToPhotoRequest(filePath:String) : String{
    val last2byte = "00000011".toInt(2).toChar()
    val last4byte = "00001111".toInt(2).toChar()
    val last6byte = "00111111".toInt(2).toChar()
    val lead6byte = "11111100".toInt(2).toChar()
    val lead4byte = "11110000".toInt(2).toChar()
    val lead2byte = "11000000".toInt(2).toChar()
    val encodeTable = charArrayOf(
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z',
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '+',
        '/'
    )

    var var7 : ByteArray = ByteArray(0)
    try {
        val file = File(filePath)
        val bos = ByteArrayOutputStream(file.length().toInt())
        val bis = BufferedInputStream(FileInputStream(file))
        bos.use {
            var buffer = ByteArray(1024)
            var len : Int = 0
            do{
                len = bis.read(buffer,0,1024)
                if (len != -1) it.write(buffer,0,len)
            }while (len != -1)
            var7 = it.toByteArray()
        }
    }catch (e:IOException){
        Log.d("zxzzxzzxz",e.toString())
    }

    //base64编码
    // (var7.size == 0) //Toast.makeText(HomeDemoApplication.context,"图片大小为0 error",Toast.LENGTH_SHORT).show()
    val sb = StringBuilder((var7.size.toDouble() * 1.34).toInt() + 3)
    var num : Int = 0
    var currentByte : Int = 0
    for(i in 0 until var7.size){

        num %= 8
        while (num < 8) {
            when (num) {
                0 -> {
                    currentByte = var7[i].toInt() and lead6byte.toInt()
                    currentByte = currentByte ushr 2
                }
                1, 3, 5 -> {
                }
                2 -> currentByte = var7[i].toInt() and last6byte.toInt()
                4 -> {
                    currentByte = var7[i].toInt() and last4byte.toInt()
                    currentByte = currentByte shl 2
                    if ((i + 1) < var7.size) {
                        currentByte = currentByte or ((var7[i + 1].toInt() and lead2byte.toInt()) ushr 6)
                    }
                }
                6 -> {
                    currentByte = var7[i].toInt() and last2byte.toInt()
                    currentByte = currentByte shl 4
                    if ((i + 1) < var7.size) {
                        currentByte = currentByte or ((var7[i + 1].toInt() and lead4byte.toInt()) ushr 4)
                        val m = currentByte
                    }
                }
                else -> {
                }
            }
            sb.append(encodeTable.get(currentByte))
            num += 6
        }
    }
    if (sb.length % 4 != 0) {
        var i = 4 - sb.length % 4
        while (i > 0) {
            sb.append("=")
            --i
        }
    }

    return "image="+URLEncoder.encode(sb.toString(), "UTF-8") + "&with_face=1"
}
