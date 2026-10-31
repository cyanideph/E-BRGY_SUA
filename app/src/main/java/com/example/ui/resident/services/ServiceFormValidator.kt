package com.example.ui.resident.services

/** Service Form Validator - validates service request form inputs.
 * 
 *  Follows constitution principle VI: data integrity.
 *  All incoming data validated at the repository layer before writes.
 */
object ServiceFormValidator {

    /** Validate service type */
    fun validateServiceType(serviceType: String): String {
        if (serviceType.isEmpty()) {
            return "Service type is required"
        }
        return "" // valid
    }

    /** Validate description */
    fun validateDescription(description: String): String {
        if (description.isEmpty()) {
            return "Description is required"
        }
        if (description.length < 10) {
            return "Description must be at least 10 characters"
        }
        return "" // valid
    }

    /** Validate resident name */
    fun validateResidentName(name: String): String {
        if (name.isEmpty()) {
            return "Full name is required"
        }
        if (name.length < 2) {
            return "Full name must be at least 2 characters"
        }
        return "" // valid
    }

    /** Validate address */
    fun validateAddress(address: String): String {
        if (address.isEmpty()) {
            return "Barangay address is required"
        }
        if (address.length < 5) {
            return "Address must be at least 5 characters"
        }
        return "" // valid
    }

    /** Validate contact number */
    fun validateContactNumber(phone: String): String {
        if (phone.isEmpty()) {
            return "Contact number is required"
        }
        if (phone.length < 10) {
            return "Contact number must be at least 10 digits"
        }
        return "" // valid
    }

    /** Validate all fields - returns list of error messages */
    fun validate(
        serviceType: String,
        description: String,
        residentName: String,
        address: String,
        contactNumber: String
    ): List<String> {
        val errors = mutableListOf<String>()

        val st = validateServiceType(serviceType)
        if (st.isNotEmpty()) errors.add(st)

        val desc = validateDescription(description)
        if (desc.isNotEmpty()) errors.add(desc)

        val name = validateResidentName(residentName)
        if (name.isNotEmpty()) errors.add(name)

        val addr = validateAddress(address)
        if (addr.isNotEmpty()) errors.add(addr)

        val ph = validateContactNumber(contactNumber)
        if (ph.isNotEmpty()) errors.add(ph)

        return errors
    }
}