package com.slyworks.cloudwatch

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Joshua Sylvanus, 6:13 PM, 3/24/2022.
 */
class DataManagerTest{
    @Test
    public fun instantiation_isWorking(){
        assertNotNull(DataManager.toString())
    }
}