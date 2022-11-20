package app.slyworks.cloudwatch

import app.slyworks.utils.PreferenceManager
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Joshua Sylvanus, 4:14 AM, 27-Oct-22.
 */

class PreferenceManagerTest{
    @Test
    fun whenSetIsCalledWithUnSupportedValueType_anExceptionIsThrown(){
        val preferenceManager = mock(PreferenceManager::class)
    }
}