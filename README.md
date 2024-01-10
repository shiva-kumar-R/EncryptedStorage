# Encrypted Storage
Android Library for encryption/decryption of data

## How to install via GRADLE

- Add the following code in your system.gradle file:
```
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

- Add the following dependencies:
```
dependencies {
   implementation 'com.github.shiva-kumar-R:encryptedstorage:Tag'
}
```

## How to use

- Create an instance of CryptoManagerImpl.

```
val cryptoManager = CryptoManagerImpl(cipherConfig)
```

- It takes cipherConfig as optional parameter by which you can update the configuration of encryption/decryption process.
- Using config you can set Algorithm, BlockMode and Padding.

```
Default values are -

Algorithm = KeyProperties.KEY_ALGORITHM_AES,
BlockMode = KeyProperties.BLOCK_MODE_CBC,
Padding = KeyProperties.ENCRYPTION_PADDING_PKCS7
```

- This library can be used for generic encryption/decryption of local data storage/can be used with datastore.

## Normal Storage

- To encrypt, Create a file in local storage
- Create a FileOutputStream for destination file and data from the UI/component can be encodedToByteArray() and passed to encrypt()

```
cryptoManager.encrypt(bytes, fos) //bytes is data in byteArray, fos is file target.
```

- To decrypt, Read the data from created file.
- Create a FileInputStream to read bytes via decrypt()

```
cryptoManager.decrypt(FileInputStream(file))
```
- It returns byteArray which can be converted to String using decodeToString()

## DataStore ie PreferenceDataStore

- Process is similar to normal storage but, a serializer from datastore implementation of the data class is needed. Example is shown below

```
private val Context.dataStore by dataStore(
        fileName = "usersettings.json",
        serializer = UserSettingSerializer(CryptoManagerImpl())
    )
```

- You can pass the instance of CryptoManagerImpl to Serializer which overrides readFrom and writeTo functions of datastore serializer. You can call encrypt and decrypt methods from here. (For more info, understand how datastore serialization works.)

```
override suspend fun readFrom(input: InputStream): UserSettings {
    val decryptedBytes = cryptoManager.decrypt(
        inputStream = input
    )
    return try {
        Json.decodeFromString(
            deserializer = UserSettings.serializer(),
            string = decryptedBytes.decodeToString()
        )
    } catch (e: SerializationException) {
        e.printStackTrace()
        defaultValue
    }
}

override suspend fun writeTo(t: UserSettings, output: OutputStream) {
    val data = Json.encodeToString(
        serializer = UserSettings.serializer(),
        value = t
    )
    cryptoManager.encrypt(
        byteArray = data.encodeToByteArray(),
        outputStream = output
    )
}
```

