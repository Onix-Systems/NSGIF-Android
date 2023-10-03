#ifndef NSGIF_H
#define NSGIF_H

#include <stdbool.h>
#include <inttypes.h>
#include "stdlib.h"
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include "utils/log.h"

/*	Maximum colour table size
*/
#define GIF_MAX_COLOURS 256

/*	Internal flag that the colour table needs to be processed
*/
#define GIF_PROCESS_COLOURS 0xaa000000

/*	Internal flag that a frame is invalid/unprocessed
*/
#define GIF_INVALID_FRAME -1

/*	Maximum LZW bits available
*/
#define GIF_MAX_LZW 12

/* Transparent colour
*/
#define GIF_TRANSPARENT_COLOUR 0x00

/*	GIF Flags
*/
#define GIF_FRAME_COMBINE 1
#define GIF_FRAME_CLEAR 2
#define GIF_FRAME_RESTORE 3
#define GIF_FRAME_QUIRKS_RESTORE 4
#define GIF_IMAGE_SEPARATOR 0x2c
#define GIF_INTERLACE_MASK 0x40
#define GIF_COLOUR_TABLE_MASK 0x80
#define GIF_COLOUR_TABLE_SIZE_MASK 0x07
#define GIF_EXTENSION_INTRODUCER 0x21
#define GIF_EXTENSION_GRAPHIC_CONTROL 0xf9
#define GIF_DISPOSAL_MASK 0x1c
#define GIF_TRANSPARENCY_MASK 0x01
#define GIF_EXTENSION_COMMENT 0xfe
#define GIF_EXTENSION_PLAIN_TEXT 0x01
#define GIF_EXTENSION_APPLICATION 0xff
#define GIF_BLOCK_TERMINATOR 0x00
#define GIF_TRAILER 0x3b

class nsgif
{
public:
    /*	Error return values
    */
    enum gif_result
    {
        GIF_WORKING = 1,
        GIF_OK = 0,
        GIF_INSUFFICIENT_FRAME_DATA = -1,
        GIF_FRAME_DATA_ERROR = -2,
        GIF_INSUFFICIENT_DATA = -3,
        GIF_DATA_ERROR = -4,
        GIF_INSUFFICIENT_MEMORY = -5,
        GIF_FRAME_NO_DISPLAY = -6,
        GIF_END_OF_FRAME = -7
    };

    /*	The GIF frame data
    */
    struct gif_frame
    {
        bool display;				/**< whether the frame should be displayed/animated */
        unsigned int frame_delay;		/**< delay (in cs) before animating the frame */
        /**	Internal members are listed below
        */
        unsigned int frame_pointer;		/**< offset (in bytes) to the GIF frame data */
        bool virgin;				/**< whether the frame has previously been used */
        bool opaque;				/**< whether the frame is totally opaque */
        bool redraw_required;			/**< whether a forcable screen redraw is required */
        unsigned char disposal_method;		/**< how the previous frame should be disposed; affects plotting */
        bool transparency;	 		/**< whether we acknoledge transparency */
        unsigned char transparency_index;	/**< the index designating a transparent pixel */
        unsigned int redraw_x;			/**< x co-ordinate of redraw rectangle */
        unsigned int redraw_y;			/**< y co-ordinate of redraw rectangle */
        unsigned int redraw_width;		/**< width of redraw rectangle */
        unsigned int redraw_height;		/**< height of redraw rectangle */
    };

    /*	The GIF animation data
    */
    struct gif_animation
    {
        unsigned char *gif_data;			/**< pointer to GIF data */
        unsigned int width;				/**< width of GIF (may increase during decoding) */
        unsigned int height;				/**< heigth of GIF (may increase during decoding) */
        unsigned int frame_count;			/**< number of frames decoded */
        unsigned int frame_count_partial;		/**< number of frames partially decoded */
        gif_frame *frames;				/**< decoded frames */
        int decoded_frame;				/**< current frame decoded to bitmap */
        void *frame_image;				/**< currently decoded image; stored as bitmap from bitmap_create callback */
        int loop_count;					/**< number of times to loop animation */
        gif_result current_error;			/**< current error type, or 0 for none*/
        /**	Internal members are listed below
        */
        unsigned int buffer_position;			/**< current index into GIF data */
        unsigned int buffer_size;			/**< total number of bytes of GIF data available */
        unsigned int frame_holders;			/**< current number of frame holders */
        unsigned int background_index;			/**< index in the colour table for the background colour */
        unsigned int aspect_ratio;			/**< image aspect ratio (ignored) */
        unsigned int colour_table_size;		/**< size of colour table (in entries) */
        bool global_colours;				/**< whether the GIF has a global colour table */
        unsigned int *global_colour_table;		/**< global colour table */
        unsigned int *local_colour_table;		/**< local colour table */
    };

    nsgif(signed char *array, size_t size);
    nsgif(FILE *file,size_t size);
    virtual ~nsgif();
    bool decode_frame(unsigned int frame);
    unsigned char* get_image();
    int get_width();
    int get_height();
    int get_time(unsigned int frame);
    int get_count();
    int getId();
    void setId(int _id);
    int get_current_frame();
    int get_gif_result();
protected:
    void *gif_bitmap_cb_create(int width, int height);
    void gif_bitmap_cb_destroy(void *bitmap);
    unsigned char *gif_bitmap_cb_get_buffer(void *bitmap);
    void gif_bitmap_cb_set_opaque(void *bitmap, bool opaque);
    bool gif_bitmap_cb_test_opaque(void *bitmap);
    void gif_bitmap_cb_modified(void *bitmap);
private:
    int id;
    int error_code;
    gif_animation gif_struct;
    gif_animation *gif_pointer;
    unsigned char *gif_data;
    bool clear_image;
    //
    gif_result gif_decode_frame(gif_animation *gif, unsigned int frame);
    void gif_finalise(gif_animation *gif);
    gif_result gif_initialise(gif_animation *gif, size_t size, unsigned char *data);
    gif_result gif_initialise_frame(gif_animation *gif);
    gif_result gif_initialise_frame_extensions(gif_animation *gif, const int frame);
    gif_result gif_initialise_sprite(gif_animation *gif, unsigned int width, unsigned int height);
    gif_result gif_skip_frame_extensions(gif_animation *gif);
    //
    /*	General LZW values. They are shared for all GIFs being decoded, and
        thus we can't handle progressive decoding efficiently without having
        the data for each image which would use an extra 10Kb or so per GIF.
    */
    unsigned char buf[4];
    unsigned char *direct;
    int maskTbl[16];
    int table[2][(1 << GIF_MAX_LZW)];
    unsigned char stack[(1 << GIF_MAX_LZW) * 2];
    unsigned char *stack_pointer;
    int code_size, set_code_size;
    int max_code, max_code_size;
    int clear_code, end_code;
    int curbit, lastbit, last_byte;
    int firstcode, oldcode;
    bool zero_data_block;
    bool get_done;
    //
    void gif_init_LZW(gif_animation *gif);
    int gif_next_code(gif_animation *gif, int code_size);
    unsigned int gif_interlaced_line(int height, int y);
    bool gif_next_LZW(gif_animation *gif);
};

#endif // NSGIF_H
