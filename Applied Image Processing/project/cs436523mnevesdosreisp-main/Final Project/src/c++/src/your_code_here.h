#pragma once
#include <algorithm>
#include <array>
#include <cmath>
#include <numeric>
#include <tuple>
#include <vector>

#include "helpers.h"



/// <summary>
/// Calculates each weights from source image, according with the approach from the paper suggested by the professors.
/// For each pixel, calculate the distance from the 4 nearest neighbours.
/// Save the weight in a glm::vec4 vector, which is a channel from the RGBA image, hence its use, for convinience.
/// </summary>
/// <param name="image">The image used to calculate the weights.</param>
/// <returns>ImageRGBA, each channel of RGBA image correspondes to the weight of one of the neighbours .</returns>
ImageRGBA calculateWeights(const ImageRGB& image)
{
    ImageRGBA result = ImageRGBA(image.width, image.height);

    const float BETHA = 0.2f;

    #pragma omp parallel for
    for (int x = 0; x < result.width; x++) {
        for (int y = 0; y < result.height; y++) {

            float w1 = 0.0f;
            float w2 = 0.0f;
            float w3 = 0.0f;
            float w4 = 0.0f;


            // Ignore neighbours outside the image
            if (x - 1 < 0){
            } else {
                // Calculate the distance 
                auto dist1_x = abs(255*image.data[y * image.width + x].x - 255*image.data[y * image.width + (x - 1)].x);
                auto dist1_y = abs(255*image.data[y * image.width + x].y - 255*image.data[y * image.width + (x - 1)].y);
                auto dist1_z = abs(255*image.data[y * image.width + x].z - 255*image.data[y * image.width + (x - 1)].z);
                auto addup1 = dist1_x + dist1_y + dist1_z;

                // Use formula provided in the paper
                w1 = std::exp(-BETHA * addup1);
            }

            if (x + 1 >= result.width) {
            } else {
                auto dist2_x = abs(255*image.data[y * image.width + x].x - 255*image.data[y * image.width + (x + 1)].x);
                auto dist2_y = abs(255 * image.data[y * image.width + x].y - 255 * image.data[y * image.width + (x + 1)].y);
                auto dist2_z = abs(255 * image.data[y * image.width + x].z - 255 * image.data[y * image.width + (x + 1)].z);
                auto addup2 = dist2_x + dist2_y + dist2_z;
                w2 = std::exp(-BETHA * addup2);
            }

            if (y - 1 < 0) {
            } else {
                auto dist3_x = abs(255*image.data[y * image.width + x].x - 255*image.data[(y - 1) * image.width + x].x);
                auto dist3_y = abs(255 * image.data[y * image.width + x].y - 255 * image.data[(y - 1) * image.width + x].y);
                auto dist3_z = abs(255 * image.data[y * image.width + x].z - 255 * image.data[(y - 1) * image.width + x].z);
                auto addup3 = dist3_x + dist3_y + dist3_z;
                w3 = std::exp(-BETHA * addup3);
            }

            if (y + 1 >= result.height) {
            } else {
                auto dist4_x = abs(255*image.data[y * image.width + x].x - 255*image.data[(y + 1) * image.width + x].x);
                auto dist4_y = abs(255 * image.data[y * image.width + x].y - 255 * image.data[(y + 1) * image.width + x].y);
                auto dist4_z = abs(255 * image.data[y * image.width + x].z - 255 * image.data[(y + 1) * image.width + x].z);
                auto addup4 = dist4_x + dist4_y + dist4_z;
                w4 = std::exp(-BETHA * addup4);
            }

            result.data[y * result.width + x] = glm::vec4(w1, w2, w3, w4);
        }
    }

    return result;
}

/// <summary>
/// Diffuses the RGBA image with the scribbles, applying the Poisson diffusion technique described in the paper
/// For each pixel, calculates the importance of the 4 nearest neighbours, using the weights.
/// Assigns the normalized value to the current pixel
/// 
/// </summary>
/// <param name="image_scribbles">The RGBA image with the scribbles.</param>
/// <param name="image_src">The source image.</param>
/// <returns>ImageRGBA, image with the scribbles now diffused .</returns>
ImageRGBA difuseImage(const ImageRGBA& image_scribbles, const ImageRGB& image_src)
{

    auto result = ImageRGBA(image_scribbles.width, image_scribbles.height);
    auto result_next = ImageRGBA(image_scribbles.width, image_scribbles.height);
    
    // Initializes result to perform Poisson solving
    #pragma omp parallel for
    for (int x = 0; x < image_scribbles.width; x++) {
        for (int y = 0; y < image_scribbles.height; y++) {
            result.data[y * result.width + x] = image_scribbles.data[y * image_scribbles.width + x];
        }
    }

    // Calculates the weights for all image pixels
    auto weights = calculateWeights(image_src);

    int num_iters = 9000;
    for (auto iter = 0; iter < num_iters; iter++) {
        if (iter % 500 == 0) {
            // Print progress info every 500 iteartions.
            std::cout << "[" << iter << "/" << num_iters << "] Solving Poisson equation..." << std::endl;
        }
        #pragma omp parallel for
        for (int x = 0; x < result.width; x++) {
            for (int y = 0; y < result.height; y++) {
                glm::vec3 p1 = glm::vec3(0, 0, 0); 
                glm::vec3 p2 = glm::vec3(0, 0, 0);
                glm::vec3 p3 = glm::vec3(0, 0, 0);
                glm::vec3 p4 = glm::vec3(0, 0, 0);

                float w1 = 0.0f;
                float w2 = 0.0f;
                float w3 = 0.0f;
                float w4 = 0.0f;


                int n_pixels = 0;

                //ignores pixels outside the image
                if (x - 1 < 0) {
                    //check if the pixel is visible
                } else if (result.data[y * result.width + (x - 1)].w != 0) {
                    //gets the weight
                    w1 = weights.data[y * weights.width + x].x;
                    p1 = glm::vec3(result.data[y * result.width + (x - 1)]);
                    n_pixels++;
                }

                if (x + 1 >= result.width) {
                } else if (result.data[y * result.width + (x + 1)].w != 0) {
                    w2 = weights.data[y * weights.width + x].y;
                    p2 = glm::vec3(result.data[y * result.width + (x + 1)]);
                    n_pixels++;
                }

                if (y - 1 < 0) {
                } else if (result.data[(y - 1) * result.width + x].w != 0) {
                    w3 = weights.data[y * weights.width + x].z;
                    p3 = glm::vec3(result.data[(y - 1) * result.width + x]);
                    n_pixels++;
                }

                if (y + 1 >= result.height) {
                } else if (result.data[(y + 1) * result.width + x].w != 0) {
                    w4 = weights.data[y * weights.width + x].w;
                    p4 = glm::vec3(result.data[(y + 1) * result.width + x]);
                    n_pixels++;
                }

                //If no pixels are available, skip
                if (n_pixels == 0) {
                    continue;
                }
                result_next.data[y * result_next.width + x] = glm::vec4(((p1*w1 + p2*w2 + p3*w3 + p4*w4)) / (w1+w2+w3+w4), 1);
               
            }
        }
        std::swap(result, result_next);
    }

    return result;

}



/// <summary>
/// Applies the bilateral filter on the given source image, with respect to depth.
/// </summary>
/// <param name="image_src">The image to be filtered.</param>
/// <param name="image_depth">The image guide used for calculating the tonal distances between pixel values.</param>
/// <param name="aperture_size">The "lens" aperture size.</param>
/// <param name="depth_focus">Value of the depth where the pixels will be focused.</param>
/// <returns>ImageFloat, the filtered disparity.</returns>
template <typename T>
ImageRGB jointBilateralFilter(const ImageRGB& image_src, const Image<T>& image_depth, const float aperture_size, const float depth_focus)
{
    // We assume both images have matching dimensions.
    assert(image_src.width == image_depth.width && image_src.height == image_depth.height);

    // Empty output image.
    auto result = ImageRGB(image_src.width, image_src.height);
    #pragma omp parallel for
    for (int x = 0; x < result.width; x++) {
        for (int y = 0; y < result.height; y++) {

            //Circle of confusion
            int coc;

            //Preventing division by 0
            if (image_depth.data[y * image_depth.width + x].x == 0.0f) {
                coc = 3;
            } else {
                //Calculating Circle of confusion
                auto dist_xyz = abs(glm::vec3(255 * image_depth.data[y * image_depth.width + x].x - depth_focus, 255 * image_depth.data[y * image_depth.width + x].y - depth_focus,
                                        255 * image_depth.data[y * image_depth.width + x].z - depth_focus)
                    / glm::vec3(255 * image_depth.data[y * image_depth.width + x].x, 255 * image_depth.data[y * image_depth.width + x].y, 255 * image_depth.data[y * image_depth.width + x].z));
                float dist = dist_xyz.x + dist_xyz.y + dist_xyz.z;

                coc = (int)std::ceil(aperture_size * dist);
                
                //Simple adjustments to make sure the sigma value is "good" for the gaussian
                if (coc < 3) {
                #pragma omp critical
                    coc = 3;
                //Capping high values to make execution faster
                } else if (coc > 60) {
                    #pragma omp critical
                    coc = 61;
                }
                else if (coc % 2 == 0) {
                #pragma omp critical
                    coc++;
                }
            }

            const float sigma = (coc - 1) / 2 / 3.2f;
            float weights = 0;
            glm::vec3 weights_pixels = glm::vec3(0,0,0);

            //Same approach to the algorithm in lab3
            //Visit the neighborhood and compute the weights, considering the source image
            //Assign the average of the weights to the current pixel
            for (int j = x - ((coc - 1) / 2); j <= x + ((coc - 1) / 2); j++) {
                for (int k = y - ((coc - 1) / 2); k <= y + ((coc - 1) / 2); k++) {
                    if (j >= 0 && k >= 0 && j < result.width && k < result.height) {

                        float dist = std::sqrt(std::pow(x - j, 2) + std::pow(y - k, 2));

                        float diff_value = std::sqrt(std::pow(image_depth.data[y * image_depth.width + x].r - image_depth.data[(k * image_depth.width + j)].r, 2) 
                            + std::pow(image_depth.data[y * image_depth.width + x].g - image_depth.data[(k * image_depth.width + j)].g, 2) + 
                            std::pow(image_depth.data[y * image_depth.width + x].b - image_depth.data[(k * image_depth.width + j)].b, 2));

                        float w_i = gauss(dist, sigma) * gauss(diff_value, sigma);

                        weights_pixels += w_i * image_src.data[k * image_src.width + j];
                        weights += w_i;
                    }
                }
            }
            result.data[y * result.width + x] = weights_pixels / weights;
        }
    }

    return result;
}



