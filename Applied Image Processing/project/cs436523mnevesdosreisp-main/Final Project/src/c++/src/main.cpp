#include "your_code_here.h"

static const std::filesystem::path dataDirPath { DATA_DIR };
static const std::filesystem::path outDirPath { OUTPUT_DIR };


/// <summary>
/// Main method, called by python file. 
/// </summary>
/// <returns>0</returns>
int main(int argc, char* argv[])
{
    float aperture_size=5.0f, depth_focus=200.0f;

    // Diferent executions depending if the CNN is used or not
    // Mode 0: Depth calculated with scribbles
    // Mode 1: Depth calculated with the help of CNN
    int mode = 0;
    
    // Getting the aperture size and depth focus from python
    if (argc > 1) {
        mode = std::stoi(argv[1]);
        aperture_size = std::stof(argv[2]);
        depth_focus = std::stof(argv[3]);
    }

    std::cout << mode << std::endl;
    std::cout << aperture_size << std::endl;
    std::cout << depth_focus << std::endl;
    // Do not add any noise to the saved images.
    std::srand(unsigned(4733668));
    const float im_write_noise_level = 0.0f;

    std::chrono::steady_clock::time_point time_start, time_end;
    printOpenMPStatus();
    
    
    // 0. Load inputs from files. 
    ImageRGB image = ImageRGB(dataDirPath / "python/image.jpg");

    // Test save the inputs.
    image.writeToFile(outDirPath / "0_src_image.png", 1.0f, im_write_noise_level);

    if (mode == 0) {
        // 1. Load scribbles from inputs folder and saving it in outputs folder.
        ImageRGBA src_scribles = ImageRGBA(dataDirPath / "python/scribles.png");
        src_scribles.writeToFile(outDirPath / "1_src_scribbles.png", 1.0f, im_write_noise_level);
        

        // 2. Difusing the scribbles with Poisson image editing, estimating the depth
        time_start = std::chrono::steady_clock::now();
        ImageRGBA difused = difuseImage(src_scribles, image);
        time_end = std::chrono::steady_clock::now();
        std::cout << "[1] Diffuse Scribbles | Elapsed time: "
                  << std::chrono::duration_cast<std::chrono::microseconds>(time_end - time_start).count() / 1e3f
                  << " ms" << std::endl;
        // 3. Saving intermediate results
        difused.writeToFile(outDirPath / "2_diffused_image.png", 1.0f, im_write_noise_level);

        time_start = std::chrono::steady_clock::now();
        // 4. Simulate depth-of-field using joint bilateral filter.
        auto depth_of_field = jointBilateralFilter(image, difused, aperture_size, depth_focus);
        time_end = std::chrono::steady_clock::now();
        std::cout << "[2] Diffused + Src Image -> Depth of Field | Elapsed time: "
                  << std::chrono::duration_cast<std::chrono::microseconds>(time_end - time_start).count() / 1e3f
                  << " ms" << std::endl;
        // 5. Save result in outputs folder
        depth_of_field.writeToFile(outDirPath / "3_depth_of_field_image.png", 1.0f, im_write_noise_level);

        std::cout << "All done!" << std::endl;
    }

    if (mode == 1) {
        // 1. If depth was estimated by CNN, load it from inputs and save it in outputs
        ImageRGB difused = ImageRGB(dataDirPath / "python/depth.png");
        difused.writeToFile(outDirPath / "2_diffused_image_cnn.png", 1.0f, im_write_noise_level);

        time_start = std::chrono::steady_clock::now();

        // 2. Simulate depth-of-field using joint bilateral filter.
        auto depth_of_field = jointBilateralFilter(image, difused, aperture_size, depth_focus);
        time_end = std::chrono::steady_clock::now();
        std::cout << "[2] Diffused + Src Image -> Depth of Field | Elapsed time: "
                  << std::chrono::duration_cast<std::chrono::microseconds>(time_end - time_start).count() / 1e3f
                  << " ms" << std::endl;
        // 3. Save result in outputs folder
        depth_of_field.writeToFile(outDirPath / "3_depth_of_field_image.png", 1.0f, im_write_noise_level);

        std::cout << "All done!" << std::endl;
    }
  
    return 0;
}
