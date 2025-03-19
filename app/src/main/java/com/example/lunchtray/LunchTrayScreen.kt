/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import android.app.Activity
import android.media.audiofx.AudioEffect.Descriptor
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.BaseMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuPreview
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum
enum class pantallas_enum (@StringRes val title:Int){
    Inicio(title=R.string.start_order)
    ,Principal(title=R.string.choose_entree)
    ,Guarnicion(title=R.string.choose_side_dish)
    ,Acompañamiento(title=R.string.choose_accompaniment)
    ,Confirmacion(title=R.string.order_summary)
}
// TODO: AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp() {
    // TODO: Create Controller and initialization
    val navController=rememberNavController()
    val back by navController.currentBackStackEntryAsState()
    val current = pantallas_enum.valueOf(
        back?.destination?.route ?: pantallas_enum.Inicio.name
    )
    val activity= LocalContext.current as? Activity
    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            // TODO: AppBar
            CenterAlignedTopAppBar(
                title={ stringResource(current.title)},
                navigationIcon = {
                    if(current.title!=pantallas_enum.Inicio.title)
                    {
                        IconButton(onClick = {navController.navigateUp()}){
                    Icon( Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")}
                    }
                }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = pantallas_enum.Inicio.name,
            modifier = Modifier.padding(innerPadding)
        ){
            val mod=Modifier.fillMaxSize()
            composable(route=pantallas_enum.Inicio.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate(pantallas_enum.Principal.name)
                    } ,
                    modifier = mod
                )
            }
            composable(route=pantallas_enum.Principal.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.navigate(pantallas_enum.Inicio.name)
                    },
                    onNextButtonClicked = {
                        navController.navigate(pantallas_enum.Guarnicion.name)
                    },
                    onSelectionChanged = {  selec->
                        viewModel.updateEntree(selec)
                    },
                    modifier = mod
                )

            }
            composable(route=pantallas_enum.Guarnicion.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.navigate(pantallas_enum.Inicio.name)
                    },
                    onNextButtonClicked = {
                        navController.navigate(pantallas_enum.Acompañamiento.name)
                    },
                    onSelectionChanged = {  selec->
                        viewModel.updateSideDish(selec)
                    },
                    modifier = mod
                )
            }
            composable(route=pantallas_enum.Acompañamiento.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.navigate(pantallas_enum.Inicio.name)
                    },
                    onNextButtonClicked =  {
                        navController.navigate(pantallas_enum.Confirmacion.name)
                    },
                    onSelectionChanged ={  selec->
                        viewModel.updateAccompaniment(selec)
                    },
                    modifier = mod
                )
            }
            composable(route=pantallas_enum.Confirmacion.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = { activity?.finish() }
                     ,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.navigate(pantallas_enum.Inicio.name)
                    },
                    modifier = mod
                )
            }
        }
    }

        // TODO: Navigation host
    }

