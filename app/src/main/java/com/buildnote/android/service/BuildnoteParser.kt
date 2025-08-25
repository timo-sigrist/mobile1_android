package com.buildnote.android.service

import com.buildnote.android.model.Material
import com.buildnote.android.model.Project

interface BuildnoteParser {
    fun parseMaterials(raw: String): List<Material>
    fun parseProjects(raw: String): List<Project>
}