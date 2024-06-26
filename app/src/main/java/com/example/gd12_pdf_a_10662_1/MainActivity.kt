package com.example.gd12_pdf_a_10662_1

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gd12_pdf_a_10662_1.databinding.ActivityMainBinding
import com.itextpdf.barcodes.BarcodeQRCode
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.element.Image
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)

        binding!!.buttonSave.setOnClickListener{
            val nama = binding!!.editTextName.text.toString()
            val umur = binding!!.editTextUmur.text.toString()
            val tlp = binding!!.editTextHP.text.toString()
            val alamat = binding!!.editTextAlamat.text.toString()
            val kampus = binding!!.editTextKampus.text.toString()


            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    if(nama.isEmpty()&& umur.isEmpty() && tlp.isEmpty() && alamat.isEmpty() && kampus.isEmpty()){
                        Toast.makeText(applicationContext,"Semuanya Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                    }else{
                        createPDF(nama, umur, tlp, alamat, kampus)
                    }
                }
            } catch (e:FileNotFoundException){
                e.printStackTrace()
            }
        }
    }
    @SuppressLint("ObsoleteSdkint")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(
        FileNotFoundException::class
    )
    private fun createPDF(nama: String, umur: String, tlp: String, alamat: String, kampus: String) {
        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val file = File(pdfPath, "pdf_10662.pdf")
        FileOutputStream(file)

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)
        pdfDocument.defaultPageSize = PageSize.A4
        document.setMargins(5f, 5f,5f,5f)
        @SuppressLint("UseCompatLoadingForDrawables") val d = getDrawable(R.drawable.sinobu)



        val bitmap = (d as BitmapDrawable?)!!.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bitMapData = stream.toByteArray()
        val imageData = ImageDataFactory.create(bitMapData)
        val image = Image(imageData)
        val namapengguna = Paragraph("Identitas Pengguna").setBold().setFontSize(24f)
            .setTextAlignment(TextAlignment.CENTER)
        val group = Paragraph(
            """
                Berikut Adalah 
                Nama Pengguna UAJY 2022/2023
                """.trimIndent()
        ).setTextAlignment(TextAlignment.CENTER).setFontSize(12f)

        val width = floatArrayOf(100f,100f)
        val table = Table(width)

        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
        table.addCell(Cell().add(Paragraph("Nama Diri")))
        table.addCell(Cell().add(Paragraph(nama)))
        table.addCell(Cell().add(Paragraph("Umur")))
        table.addCell(Cell().add(Paragraph(umur)))
        table.addCell(Cell().add(Paragraph("No Telepon")))
        table.addCell(Cell().add(Paragraph(tlp)))
        table.addCell(Cell().add(Paragraph("Alamat Domisili")))
        table.addCell(Cell().add(Paragraph(alamat)))
        table.addCell(Cell().add(Paragraph("Nama Kampus")))
        table.addCell(Cell().add(Paragraph(kampus)))
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        table.addCell(Cell().add(Paragraph("Tanggal Buat PDF")))
        table.addCell(Cell().add(Paragraph(LocalDate.now().format(dateTimeFormatter))))
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss a")
        table.addCell(Cell().add(Paragraph("Pukul Pembuatan")))
        table.addCell(Cell().add(Paragraph(LocalTime.now().format(timeFormatter))))

        val barcodeQRCode = BarcodeQRCode(
            """
                $nama
                $umur
                $tlp
                $alamat
                $kampus
                ${LocalDate.now().format(dateTimeFormatter)}
                ${LocalTime.now().format(timeFormatter)}
            """.trimIndent()
        )

        val qrCodeObject = barcodeQRCode.createFormXObject(ColorConstants.BLACK, pdfDocument)
        val qrCodeimage = com.itextpdf.layout.element.Image(qrCodeObject).setWidth(80f).setHorizontalAlignment(HorizontalAlignment.CENTER)

        document.add(image)
        document.add(namapengguna)
        document.add(group)
        document.add(table)
        document.add(qrCodeimage)

        document.close()
        Toast.makeText(this, "PDF Created", Toast.LENGTH_LONG).show()
    }
}