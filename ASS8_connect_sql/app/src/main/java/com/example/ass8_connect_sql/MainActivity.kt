package com.example.ass8_connect_sql

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_dialog_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val employeeList = arrayListOf<Employee>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.adapter = EmployeeAdapter(this.employeeList, applicationContext)
        recycler_view.layoutManager = LinearLayoutManager(applicationContext)
    }


    override fun onResume() {
        super.onResume()
        callEmployeedata()
    }

    fun addEmployee(v: View){

        val mDialogViews= LayoutInflater.from(this).inflate(R.layout.add_dialog_layout,null)
        val myBuilder = AlertDialog.Builder(this)
        myBuilder.setView(mDialogViews)

        val mAlertDialog= myBuilder.show()
        mAlertDialog.btnAdd.setOnClickListener(){
//            employeeList.add(
//                Employee(
//                    mAlertDialog.edt_name.text.toString(),
//                    radioButton?.text.toString(),
//                    mAlertDialog.edt_email.text.toString(),
//                    mAlertDialog.edt_salary.text.toString().toInt()
//                )
//            )

            val serv: EmployeeAPI = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EmployeeAPI::class.java)

            var selectedId: Int=mAlertDialog.radioGroup.checkedRadioButtonId
            var radioButton: RadioButton? = mAlertDialog.findViewById(selectedId)

            serv.insertEmp(
                mAlertDialog.edt_name.text.toString(),
                radioButton?.text.toString(),
                mAlertDialog.edt_email.text.toString(),
                mAlertDialog.edt_salary.text.toString().toInt()).enqueue(object : Callback<Employee>{
                override fun onResponse(call: Call<Employee>, response: Response<Employee>) {
                    if (response.isSuccessful()){
                        Toast.makeText(applicationContext,"Successfully Inserted", Toast.LENGTH_LONG).show()

                    }else{
                        Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Employee>, t: Throwable) {
                    Toast.makeText(applicationContext,"Error onFailure "+ t.message,Toast.LENGTH_LONG).show()
                }
            })
            recycler_view.adapter?.notifyDataSetChanged()
            Toast.makeText(
                applicationContext,
                "The employee is added successfully",
                Toast.LENGTH_LONG
            ).show()
            mAlertDialog.dismiss()

        }
        mAlertDialog.btnCancel.setOnClickListener(){
            mAlertDialog.dismiss()
        }


    }

    fun callEmployeedata(){
        employeeList.clear()
        val serv: EmployeeAPI= Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmployeeAPI::class.java)

        serv.retrieveEmployee()
            .enqueue(object : Callback<List<Employee>> {
                override fun onResponse(
                    call: Call<List<Employee>>,
                    response: Response<List<Employee>>
                ) {
                    response.body()?.forEach{
                       employeeList.add(Employee(it.emp_name,it.emp_gender,it.emp_email,it.emp_salary))
                    }
                    recycler_view.adapter= EmployeeAdapter(employeeList,applicationContext)

                }

                override fun onFailure(call: Call<List<Employee>>, t: Throwable) {
                    return t.printStackTrace()
                }
            })
    }

}