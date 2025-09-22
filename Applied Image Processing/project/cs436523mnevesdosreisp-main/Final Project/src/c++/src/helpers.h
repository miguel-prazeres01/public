#pragma once
/*
This file contains useful function and definitions.
Do not ever edit this file - it will not be uploaded for evaluation.
If you want to modify any of the functions here (e.g. extend triangle test to quads),
copy the function "your_code_here.h" and give it a new name.
*/

#include <framework/disable_all_warnings.h>
DISABLE_WARNINGS_PUSH()
#include <glm/vec2.hpp>
#include <glm/vec3.hpp>
#include <glm/geometric.hpp>
#include <glm/gtx/matrix_transform_2d.hpp>
DISABLE_WARNINGS_POP()

#include <cassert>
#include <chrono>
#include <cmath>
#include <execution>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <memory>
#include <string>
#include <array>

#ifdef _OPENMP
// Only if OpenMP is enabled.
#include <omp.h>
#endif

#include <framework/image.h>

/// <summary>
/// A value used to mark invalid pixels.
/// </summary>
static constexpr float INVALID_VALUE = -1e10f;

/// <summary>
/// Aliases for Image classes.
/// </summary>
using ImageRGB = Image<glm::vec3>;
using ImageRGBA = Image<glm::vec4>;


/// <summary>
/// Function wrappers for passing the bilinear sampling as an argument.
/// </summary>
typedef std::function<glm::vec4(const ImageRGB& disparity, const ImageRGBA& guide, const float aperture_size, const float depth_focus)> jointBilateralFilterRGBA;
typedef std::function<glm::vec3(const ImageRGB& disparity, const ImageRGB& guide, const float aperture_size, const float depth_focus)> jointBilateralFilterRGB;

/// <summary>
/// Prints helpful information about OpenMP.
/// </summary>
void printOpenMPStatus() 
{
#ifdef _OPENMP
    // https://stackoverflow.com/questions/38281448/how-to-check-the-version-of-openmp-on-windows
    std::cout << "OpenMP version " << _OPENMP << " is ENABLED with " << omp_get_max_threads() << " threads." << std::endl;
#else
    std::cout << "OpenMP is DISABLED." << std::endl;
#endif
}

/// <summary>
/// Returns (un-normalized) pdf of a Gaussian distribution with given sigma (std dev) and mean (mu).
/// </summary>
/// <param name="x">Where to sample</param>
/// <param name="sigma">std dev</param>
/// <param name="mu">mean</param>
/// <returns>unnormalied pdf value</returns>
float gauss(const float x, const float sigma = 1.0f, const float mu = 0.0f)
{
    auto exponent = (x - mu) / sigma;
    return std::exp(-0.5f * exponent * exponent);
}





